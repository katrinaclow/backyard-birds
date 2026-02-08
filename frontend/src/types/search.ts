// Types for the search API responses

export interface SpeciesSearchResult {
  speciesCode: string;
  commonName: string;
  scientificName: string;
  category: string;
}

export interface RegionSearchResult {
  code: string;
  name: string;
  regionType: string | null;
}
