// API endpoint definitions for the explorer

export interface Parameter {
  name: string;
  type: 'string' | 'number' | 'boolean' | 'date';
  required: boolean;
  description: string;
  default?: string | number | boolean;
  options?: { value: string; label: string }[]; // For dropdowns
  min?: number;
  max?: number;
}

export interface Endpoint {
  id: string;
  name: string;
  description: string;
  method: 'GET' | 'POST';
  path: string;
  pathParams: Parameter[];
  queryParams: Parameter[];
}

export interface Category {
  id: string;
  name: string;
  description: string;
  icon: string; // Emoji for simplicity
  endpoints: Endpoint[];
}

// Common query parameters used across multiple endpoints
const observationQueryParams: Parameter[] = [
  { name: 'back', type: 'number', required: false, description: 'Days back (1-30)', default: 14, min: 1, max: 30 },
  { name: 'hotspot', type: 'boolean', required: false, description: 'Only hotspot observations', default: false },
  { name: 'includeProvisional', type: 'boolean', required: false, description: 'Include unreviewed observations', default: false },
  { name: 'maxResults', type: 'number', required: false, description: 'Maximum results (1-10000)', min: 1, max: 10000 },
  { name: 'sppLocale', type: 'string', required: false, description: 'Language for species names', default: 'en' },
  {
    name: 'cat',
    type: 'string',
    required: false,
    description: 'Category filter',
    options: [
      { value: '', label: 'All' },
      { value: 'species', label: 'Species' },
      { value: 'slash', label: 'Slash' },
      { value: 'spuh', label: 'Spuh' },
      { value: 'hybrid', label: 'Hybrid' },
      { value: 'domestic', label: 'Domestic' },
      { value: 'form', label: 'Form' },
      { value: 'issf', label: 'ISSF' },
      { value: 'intergrade', label: 'Intergrade' },
    ],
  },
  {
    name: 'sort',
    type: 'string',
    required: false,
    description: 'Sort order',
    default: 'date',
    options: [
      { value: 'date', label: 'Date' },
      { value: 'species', label: 'Species' },
    ],
  },
];

const geoParams: Parameter[] = [
  { name: 'lat', type: 'number', required: true, description: 'Latitude' },
  { name: 'lng', type: 'number', required: true, description: 'Longitude' },
  { name: 'dist', type: 'number', required: false, description: 'Distance in km', default: 25 },
];

export const categories: Category[] = [
  {
    id: 'checklist',
    name: 'Checklists',
    description: 'View submitted checklists by region or retrieve specific checklist details',
    icon: '📋',
    endpoints: [
      {
        id: 'checklist-region',
        name: 'Recent Checklists in Region',
        description: 'Get recent checklists submitted in a region',
        method: 'GET',
        path: '/api/checklists/region/{regionCode}',
        pathParams: [
          { name: 'regionCode', type: 'string', required: true, description: 'Region code (e.g., US-NY, DE-BE)' },
        ],
        queryParams: [
          {
            name: 'sortKey',
            type: 'string',
            required: false,
            description: 'Sort by',
            options: [
              { value: 'obs_dt', label: 'Observation Date' },
              { value: 'creation_dt', label: 'Creation Date' },
            ],
          },
          { name: 'maxResults', type: 'number', required: false, description: 'Max results (1-200)', default: 10, min: 1, max: 200 },
        ],
      },
      {
        id: 'checklist-region-date',
        name: 'Checklists on Date',
        description: 'Get checklists submitted in a region on a specific date',
        method: 'GET',
        path: '/api/checklists/region/{regionCode}/{year}/{month}/{day}',
        pathParams: [
          { name: 'regionCode', type: 'string', required: true, description: 'Region code (e.g., US-NY)' },
          { name: 'year', type: 'number', required: true, description: 'Year (e.g., 2024)' },
          { name: 'month', type: 'number', required: true, description: 'Month (1-12)' },
          { name: 'day', type: 'number', required: true, description: 'Day (1-31)' },
        ],
        queryParams: [
          {
            name: 'sortKey',
            type: 'string',
            required: false,
            description: 'Sort by',
            options: [
              { value: 'obs_dt', label: 'Observation Date' },
              { value: 'creation_dt', label: 'Creation Date' },
            ],
          },
          { name: 'maxResults', type: 'number', required: false, description: 'Max results (1-200)', default: 10, min: 1, max: 200 },
        ],
      },
      {
        id: 'checklist-view',
        name: 'View Checklist',
        description: 'Get details of a specific checklist by submission ID',
        method: 'GET',
        path: '/api/checklists/{subId}',
        pathParams: [
          { name: 'subId', type: 'string', required: true, description: 'Submission ID (e.g., S123456789)' },
        ],
        queryParams: [],
      },
    ],
  },
  {
    id: 'hotspot',
    name: 'Hotspots',
    description: 'Find birding hotspots by region, location, or get hotspot details',
    icon: '📍',
    endpoints: [
      {
        id: 'hotspot-region',
        name: 'Hotspots in Region',
        description: 'Get birding hotspots in a region',
        method: 'GET',
        path: '/api/hotspots/region/{regionCode}',
        pathParams: [
          { name: 'regionCode', type: 'string', required: true, description: 'Region code (e.g., US-NY, DE-BE)' },
        ],
        queryParams: [
          { name: 'back', type: 'number', required: false, description: 'Days back (1-30)', default: 14, min: 1, max: 30 },
        ],
      },
      {
        id: 'hotspot-nearby',
        name: 'Nearby Hotspots',
        description: 'Find hotspots near a geographic location',
        method: 'GET',
        path: '/api/hotspots/nearby',
        pathParams: [],
        queryParams: [
          ...geoParams,
          { name: 'back', type: 'number', required: false, description: 'Days back (1-30)', default: 14, min: 1, max: 30 },
        ],
      },
      {
        id: 'hotspot-info',
        name: 'Hotspot Info',
        description: 'Get details about a specific hotspot',
        method: 'GET',
        path: '/api/hotspots/{locId}',
        pathParams: [
          { name: 'locId', type: 'string', required: true, description: 'Location ID (e.g., L123456)' },
        ],
        queryParams: [],
      },
    ],
  },
  {
    id: 'obs',
    name: 'Observations',
    description: 'Bird sightings - recent, notable, by species, historic, and nearby',
    icon: '🐦',
    endpoints: [
      {
        id: 'obs-region',
        name: 'Recent Observations in Region',
        description: 'Get recent bird observations in a region',
        method: 'GET',
        path: '/api/observations/region/{regionCode}',
        pathParams: [
          { name: 'regionCode', type: 'string', required: true, description: 'Region code (e.g., US-NY, DE-BE)' },
        ],
        queryParams: [...observationQueryParams],
      },
      {
        id: 'obs-region-notable',
        name: 'Notable Observations in Region',
        description: 'Get notable/rare bird observations in a region',
        method: 'GET',
        path: '/api/observations/region/{regionCode}/notable',
        pathParams: [
          { name: 'regionCode', type: 'string', required: true, description: 'Region code (e.g., US-NY, DE-BE)' },
        ],
        queryParams: [...observationQueryParams],
      },
      {
        id: 'obs-region-species',
        name: 'Species Observations in Region',
        description: 'Get observations of a specific species in a region',
        method: 'GET',
        path: '/api/observations/region/{regionCode}/species/{speciesCode}',
        pathParams: [
          { name: 'regionCode', type: 'string', required: true, description: 'Region code (e.g., US-NY)' },
          { name: 'speciesCode', type: 'string', required: true, description: 'Species code (e.g., baleag for Bald Eagle)' },
        ],
        queryParams: [...observationQueryParams],
      },
      {
        id: 'obs-region-historic',
        name: 'Historic Observations',
        description: 'Get observations from a specific date in history',
        method: 'GET',
        path: '/api/observations/region/{regionCode}/historic/{year}/{month}/{day}',
        pathParams: [
          { name: 'regionCode', type: 'string', required: true, description: 'Region code (e.g., US-NY)' },
          { name: 'year', type: 'number', required: true, description: 'Year (e.g., 2024)' },
          { name: 'month', type: 'number', required: true, description: 'Month (1-12)' },
          { name: 'day', type: 'number', required: true, description: 'Day (1-31)' },
        ],
        queryParams: [
          {
            name: 'rank',
            type: 'string',
            required: false,
            description: 'Ranking method',
            options: [
              { value: 'mrec', label: 'Most Recent' },
              { value: 'create', label: 'First Reported' },
            ],
          },
          {
            name: 'detail',
            type: 'string',
            required: false,
            description: 'Detail level',
            default: 'simple',
            options: [
              { value: 'simple', label: 'Simple' },
              { value: 'full', label: 'Full' },
            ],
          },
          { name: 'hotspot', type: 'boolean', required: false, description: 'Only hotspot observations', default: false },
          { name: 'includeProvisional', type: 'boolean', required: false, description: 'Include unreviewed', default: false },
          { name: 'maxResults', type: 'number', required: false, description: 'Max results (1-10000)', min: 1, max: 10000 },
          { name: 'sppLocale', type: 'string', required: false, description: 'Language for species names', default: 'en' },
        ],
      },
      {
        id: 'obs-nearby',
        name: 'Nearby Observations',
        description: 'Get recent observations near a location',
        method: 'GET',
        path: '/api/observations/nearby',
        pathParams: [],
        queryParams: [...geoParams, ...observationQueryParams],
      },
      {
        id: 'obs-nearby-notable',
        name: 'Nearby Notable Observations',
        description: 'Get notable/rare observations near a location',
        method: 'GET',
        path: '/api/observations/nearby/notable',
        pathParams: [],
        queryParams: [...geoParams, ...observationQueryParams],
      },
      {
        id: 'obs-nearby-species',
        name: 'Nearby Species Observations',
        description: 'Get observations of a specific species near a location',
        method: 'GET',
        path: '/api/observations/nearby/species/{speciesCode}',
        pathParams: [
          { name: 'speciesCode', type: 'string', required: true, description: 'Species code (e.g., baleag)' },
        ],
        queryParams: [...geoParams, ...observationQueryParams],
      },
      {
        id: 'obs-nearby-nearest',
        name: 'Nearest Observation of Species',
        description: 'Find the nearest observation of a specific species',
        method: 'GET',
        path: '/api/observations/nearby/nearest/{speciesCode}',
        pathParams: [
          { name: 'speciesCode', type: 'string', required: true, description: 'Species code (e.g., baleag)' },
        ],
        queryParams: [...geoParams, ...observationQueryParams],
      },
    ],
  },
  {
    id: 'region',
    name: 'Regions',
    description: 'Get region information, sub-regions, and adjacent regions',
    icon: '🗺️',
    endpoints: [
      {
        id: 'region-list',
        name: 'List Sub-regions',
        description: 'Get sub-regions of a parent region',
        method: 'GET',
        path: '/api/regions/{type}/{parentCode}',
        pathParams: [
          {
            name: 'type',
            type: 'string',
            required: true,
            description: 'Region type',
            options: [
              { value: 'country', label: 'Country' },
              { value: 'subnational1', label: 'State/Province' },
              { value: 'subnational2', label: 'County' },
            ],
          },
          { name: 'parentCode', type: 'string', required: true, description: 'Parent region code (e.g., US, US-NY)' },
        ],
        queryParams: [],
      },
      {
        id: 'region-info',
        name: 'Region Info',
        description: 'Get information about a region',
        method: 'GET',
        path: '/api/regions/{regionCode}/info',
        pathParams: [
          { name: 'regionCode', type: 'string', required: true, description: 'Region code (e.g., US-NY)' },
        ],
        queryParams: [
          {
            name: 'regionNameFormat',
            type: 'string',
            required: false,
            description: 'Name format',
            options: [
              { value: 'detailed', label: 'Detailed' },
              { value: 'detailednoqual', label: 'Detailed (no qualifier)' },
              { value: 'full', label: 'Full' },
              { value: 'namequal', label: 'Name + Qualifier' },
              { value: 'nameonly', label: 'Name Only' },
              { value: 'revdetailed', label: 'Reverse Detailed' },
            ],
          },
        ],
      },
      {
        id: 'region-adjacent',
        name: 'Adjacent Regions',
        description: 'Get regions adjacent to a specified region',
        method: 'GET',
        path: '/api/regions/{regionCode}/adjacent',
        pathParams: [
          { name: 'regionCode', type: 'string', required: true, description: 'Region code (e.g., US-NY)' },
        ],
        queryParams: [],
      },
    ],
  },
  {
    id: 'specieslist',
    name: 'Species Lists',
    description: 'Get lists of species observed in a region',
    icon: '📜',
    endpoints: [
      {
        id: 'species-region',
        name: 'Species in Region',
        description: 'Get all species ever observed in a region',
        method: 'GET',
        path: '/api/species/region/{regionCode}',
        pathParams: [
          { name: 'regionCode', type: 'string', required: true, description: 'Region code (e.g., US-NY, DE-BE)' },
        ],
        queryParams: [],
      },
    ],
  },
  {
    id: 'statistics',
    name: 'Statistics',
    description: 'Top contributors and daily statistics for regions',
    icon: '📊',
    endpoints: [
      {
        id: 'stats-top100',
        name: 'Top 100 Contributors',
        description: 'Get top 100 contributors for a region on a specific date',
        method: 'GET',
        path: '/api/statistics/top100/{regionCode}/{year}/{month}/{day}',
        pathParams: [
          { name: 'regionCode', type: 'string', required: true, description: 'Region code (e.g., US-NY)' },
          { name: 'year', type: 'number', required: true, description: 'Year (e.g., 2024)' },
          { name: 'month', type: 'number', required: true, description: 'Month (1-12)' },
          { name: 'day', type: 'number', required: true, description: 'Day (1-31)' },
        ],
        queryParams: [
          {
            name: 'rankedBy',
            type: 'string',
            required: false,
            description: 'Ranking criteria',
            options: [
              { value: 'spp', label: 'Species Count' },
              { value: 'cl', label: 'Checklist Count' },
            ],
          },
          { name: 'maxResults', type: 'number', required: false, description: 'Max results (1-100)', default: 100, min: 1, max: 100 },
        ],
      },
      {
        id: 'stats-daily',
        name: 'Daily Statistics',
        description: 'Get statistics for a region on a specific date',
        method: 'GET',
        path: '/api/statistics/{regionCode}/{year}/{month}/{day}',
        pathParams: [
          { name: 'regionCode', type: 'string', required: true, description: 'Region code (e.g., US-NY)' },
          { name: 'year', type: 'number', required: true, description: 'Year (e.g., 2024)' },
          { name: 'month', type: 'number', required: true, description: 'Month (1-12)' },
          { name: 'day', type: 'number', required: true, description: 'Day (1-31)' },
        ],
        queryParams: [],
      },
    ],
  },
  {
    id: 'taxonomy',
    name: 'Taxonomy',
    description: 'Species taxonomy, forms, versions, and locales',
    icon: '🔬',
    endpoints: [
      {
        id: 'taxonomy-list',
        name: 'Taxonomy List',
        description: 'Get taxonomy data for species',
        method: 'GET',
        path: '/api/taxonomy',
        pathParams: [],
        queryParams: [
          { name: 'species', type: 'string', required: false, description: 'Comma-separated species codes (e.g., baleag,amecro)' },
          {
            name: 'cat',
            type: 'string',
            required: false,
            description: 'Category filter',
            options: [
              { value: '', label: 'All' },
              { value: 'species', label: 'Species' },
              { value: 'slash', label: 'Slash' },
              { value: 'spuh', label: 'Spuh' },
              { value: 'hybrid', label: 'Hybrid' },
              { value: 'domestic', label: 'Domestic' },
              { value: 'form', label: 'Form' },
              { value: 'issf', label: 'ISSF' },
              { value: 'intergrade', label: 'Intergrade' },
            ],
          },
          { name: 'locale', type: 'string', required: false, description: 'Language for species names', default: 'en' },
          { name: 'version', type: 'string', required: false, description: 'Taxonomy version (e.g., 2024)' },
        ],
      },
      {
        id: 'taxonomy-forms',
        name: 'Species Forms',
        description: 'Get subspecies/forms for a species',
        method: 'GET',
        path: '/api/taxonomy/{speciesCode}/forms',
        pathParams: [
          { name: 'speciesCode', type: 'string', required: true, description: 'Species code (e.g., baleag)' },
        ],
        queryParams: [],
      },
      {
        id: 'taxonomy-versions',
        name: 'Taxonomy Versions',
        description: 'Get available taxonomy versions',
        method: 'GET',
        path: '/api/taxonomy/versions',
        pathParams: [],
        queryParams: [],
      },
      {
        id: 'taxonomy-locales',
        name: 'Available Locales',
        description: 'Get available language locales for species names',
        method: 'GET',
        path: '/api/taxonomy/locales',
        pathParams: [],
        queryParams: [],
      },
      {
        id: 'taxonomy-groups',
        name: 'Species Groups',
        description: 'Get species groups by grouping type',
        method: 'GET',
        path: '/api/taxonomy/groups/{speciesGrouping}',
        pathParams: [
          {
            name: 'speciesGrouping',
            type: 'string',
            required: true,
            description: 'Grouping type',
            options: [
              { value: 'merlin', label: 'Merlin' },
              { value: 'ebird', label: 'eBird' },
            ],
          },
        ],
        queryParams: [],
      },
    ],
  },
];

// Helper to find a category by ID
export function getCategoryById(id: string): Category | undefined {
  return categories.find((c) => c.id === id);
}

// Helper to find an endpoint by ID
export function getEndpointById(categoryId: string, endpointId: string): Endpoint | undefined {
  const category = getCategoryById(categoryId);
  return category?.endpoints.find((e) => e.id === endpointId);
}
