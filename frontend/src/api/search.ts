import type { SpeciesSearchResult, RegionSearchResult } from '../types/search';

const BASE_URL = '/api/explorer/search';

export async function searchSpecies(query: string, limit = 20): Promise<SpeciesSearchResult[]> {
  if (query.length < 2) return [];

  const response = await fetch(
    `${BASE_URL}/species?q=${encodeURIComponent(query)}&limit=${limit}`
  );

  if (!response.ok) {
    throw new Error('Failed to search species');
  }

  return response.json();
}

export async function searchRegions(query: string, limit = 20): Promise<RegionSearchResult[]> {
  if (query.length < 2) return [];

  const response = await fetch(
    `${BASE_URL}/regions?q=${encodeURIComponent(query)}&limit=${limit}`
  );

  if (!response.ok) {
    throw new Error('Failed to search regions');
  }

  return response.json();
}
