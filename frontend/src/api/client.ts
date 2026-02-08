// Generic API client for making requests to the backend

export interface ApiResponse<T> {
  data: T | null;
  error: string | null;
  loading: boolean;
}

export async function fetchApi<T>(url: string): Promise<T> {
  const response = await fetch(url);

  if (!response.ok) {
    const errorText = await response.text();
    let errorMessage: string;
    try {
      const errorJson = JSON.parse(errorText);
      errorMessage = errorJson.message || errorJson.error || `HTTP ${response.status}`;
    } catch {
      errorMessage = errorText || `HTTP ${response.status}: ${response.statusText}`;
    }
    throw new Error(errorMessage);
  }

  return response.json();
}

// Build URL with path params replaced and query params appended
export function buildUrl(
  pathTemplate: string,
  pathParams: Record<string, string>,
  queryParams: Record<string, string | number | boolean>
): string {
  // Replace path parameters
  let url = pathTemplate;
  for (const [key, value] of Object.entries(pathParams)) {
    url = url.replace(`{${key}}`, encodeURIComponent(value));
  }

  // Build query string
  const queryParts: string[] = [];
  for (const [key, value] of Object.entries(queryParams)) {
    if (value !== '' && value !== undefined && value !== null) {
      queryParts.push(`${encodeURIComponent(key)}=${encodeURIComponent(String(value))}`);
    }
  }

  if (queryParts.length > 0) {
    url += '?' + queryParts.join('&');
  }

  return url;
}
