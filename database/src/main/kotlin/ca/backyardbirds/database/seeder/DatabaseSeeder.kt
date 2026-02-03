package ca.backyardbirds.database.seeder

import ca.backyardbirds.database.repository.RegionDatabaseRepository
import ca.backyardbirds.database.repository.TaxonomyDatabaseRepository
import ca.backyardbirds.domain.model.DomainResult
import ca.backyardbirds.domain.repository.RegionRepository
import ca.backyardbirds.domain.repository.TaxonomyRepository
import org.slf4j.LoggerFactory

class DatabaseSeeder(
    private val taxonomyApiRepo: TaxonomyRepository,
    private val regionApiRepo: RegionRepository,
    private val taxonomyDbRepo: TaxonomyDatabaseRepository,
    private val regionDbRepo: RegionDatabaseRepository
) {
    private val logger = LoggerFactory.getLogger(DatabaseSeeder::class.java)

    suspend fun seedIfEmpty() {
        seedTaxonomyIfEmpty()
        seedRegionsIfEmpty()
    }

    private suspend fun seedTaxonomyIfEmpty() {
        val count = taxonomyDbRepo.count()
        if (count > 0) {
            logger.info("Taxonomy table already has $count entries, skipping seed")
            return
        }

        logger.info("Seeding taxonomy from eBird API...")
        when (val result = taxonomyApiRepo.getTaxonomy()) {
            is DomainResult.Success -> {
                val entries = result.data
                when (val saveResult = taxonomyDbRepo.saveTaxonomy(entries)) {
                    is DomainResult.Success -> {
                        logger.info("Successfully seeded ${entries.size} taxonomy entries")
                    }
                    is DomainResult.Failure -> {
                        logger.error("Failed to save taxonomy: ${saveResult.message}")
                    }
                }
            }
            is DomainResult.Failure -> {
                logger.error("Failed to fetch taxonomy from API: ${result.message}")
            }
        }
    }

    private suspend fun seedRegionsIfEmpty() {
        val count = regionDbRepo.count()
        if (count > 0) {
            logger.info("Regions table already has $count entries, skipping seed")
            return
        }

        logger.info("Seeding regions from eBird API...")

        // Fetch all countries (parent is null since "world" isn't a real region)
        when (val countriesResult = regionApiRepo.getSubRegions("country", "world")) {
            is DomainResult.Success -> {
                val countries = countriesResult.data
                when (val saveResult = regionDbRepo.saveRegions(countries, "country", null)) {
                    is DomainResult.Success -> {
                        logger.info("Seeded ${countries.size} countries")
                    }
                    is DomainResult.Failure -> {
                        logger.error("Failed to save countries: ${saveResult.message}")
                        return
                    }
                }

                // Fetch subnational1 for each country
                var subnational1Count = 0
                for (country in countries) {
                    when (val subResult = regionApiRepo.getSubRegions("subnational1", country.code)) {
                        is DomainResult.Success -> {
                            val subRegions = subResult.data
                            if (subRegions.isNotEmpty()) {
                                regionDbRepo.saveRegions(subRegions, "subnational1", country.code)
                                subnational1Count += subRegions.size
                            }
                        }
                        is DomainResult.Failure -> {
                            logger.warn("Failed to fetch subnational1 for ${country.code}: ${subResult.message}")
                        }
                    }
                }
                logger.info("Seeded $subnational1Count subnational1 regions")
            }
            is DomainResult.Failure -> {
                logger.error("Failed to fetch countries from API: ${countriesResult.message}")
            }
        }
    }
}
