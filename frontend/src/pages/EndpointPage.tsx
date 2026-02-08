import { useState, useCallback } from 'react';
import { Link, useParams } from 'react-router-dom';
import { getCategoryById, getEndpointById, type Parameter } from '../data/endpoints';
import { fetchApi, buildUrl } from '../api/client';
import { SpeciesAutocomplete } from '../components/SpeciesAutocomplete';
import { RegionAutocomplete } from '../components/RegionAutocomplete';
import { ResultDisplay } from '../components/ResultDisplay';

// Form field component with autocomplete support
function FormField({
  param,
  value,
  onChange,
}: {
  param: Parameter;
  value: string;
  onChange: (value: string) => void;
}) {
  const inputClasses =
    'w-full rounded-lg border border-gray-300 px-3 py-2 focus:border-emerald-500 focus:ring-2 focus:ring-emerald-500 focus:outline-none';

  // Use species autocomplete for speciesCode fields
  if (param.name === 'speciesCode' || param.name === 'species') {
    return (
      <SpeciesAutocomplete
        value={value}
        onChange={onChange}
        required={param.required}
        placeholder={param.description}
      />
    );
  }

  // Use region autocomplete for regionCode and parentCode fields
  if (param.name === 'regionCode' || param.name === 'parentCode') {
    return (
      <RegionAutocomplete
        value={value}
        onChange={onChange}
        required={param.required}
        placeholder={param.description}
      />
    );
  }

  if (param.type === 'boolean') {
    return (
      <div className="flex items-center gap-3">
        <input
          type="checkbox"
          id={param.name}
          checked={value === 'true'}
          onChange={(e) => onChange(e.target.checked ? 'true' : 'false')}
          className="h-4 w-4 rounded border-gray-300 text-emerald-600 focus:ring-emerald-500"
        />
        <label htmlFor={param.name} className="text-gray-700">
          {param.description}
        </label>
      </div>
    );
  }

  if (param.options && param.options.length > 0) {
    return (
      <select
        id={param.name}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        className={inputClasses}
      >
        {!param.required && <option value="">-- Select --</option>}
        {param.options.map((opt) => (
          <option key={opt.value} value={opt.value}>
            {opt.label}
          </option>
        ))}
      </select>
    );
  }

  return (
    <input
      type={param.type === 'number' ? 'number' : 'text'}
      id={param.name}
      value={value}
      onChange={(e) => onChange(e.target.value)}
      placeholder={param.default !== undefined ? `Default: ${param.default}` : undefined}
      min={param.min}
      max={param.max}
      className={inputClasses}
    />
  );
}

export function EndpointPage() {
  const { categoryId, endpointId } = useParams<{ categoryId: string; endpointId: string }>();
  const category = categoryId ? getCategoryById(categoryId) : undefined;
  const endpoint =
    categoryId && endpointId ? getEndpointById(categoryId, endpointId) : undefined;

  // Form state
  const [formValues, setFormValues] = useState<Record<string, string>>({});
  const [result, setResult] = useState<unknown>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [requestUrl, setRequestUrl] = useState<string | null>(null);

  const handleFieldChange = useCallback((name: string, value: string) => {
    setFormValues((prev) => ({ ...prev, [name]: value }));
  }, []);

  const handleSubmit = useCallback(
    async (e: React.FormEvent) => {
      e.preventDefault();
      if (!endpoint) return;

      // Validate required fields
      const missingRequired: string[] = [];
      for (const param of [...endpoint.pathParams, ...endpoint.queryParams]) {
        if (param.required && !formValues[param.name]) {
          missingRequired.push(param.name);
        }
      }

      if (missingRequired.length > 0) {
        setError(`Missing required fields: ${missingRequired.join(', ')}`);
        return;
      }

      // Build path params
      const pathParams: Record<string, string> = {};
      for (const param of endpoint.pathParams) {
        if (formValues[param.name]) {
          pathParams[param.name] = formValues[param.name];
        }
      }

      // Build query params
      const queryParams: Record<string, string | number | boolean> = {};
      for (const param of endpoint.queryParams) {
        const value = formValues[param.name];
        if (value !== undefined && value !== '') {
          if (param.type === 'number') {
            queryParams[param.name] = Number(value);
          } else if (param.type === 'boolean') {
            queryParams[param.name] = value === 'true';
          } else {
            queryParams[param.name] = value;
          }
        }
      }

      const url = buildUrl(endpoint.path, pathParams, queryParams);
      setRequestUrl(url);
      setLoading(true);
      setError(null);
      setResult(null);

      try {
        const data = await fetchApi<unknown>(url);
        setResult(data);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Unknown error');
      } finally {
        setLoading(false);
      }
    },
    [endpoint, formValues]
  );

  if (!category || !endpoint) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-gray-50">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-gray-900">Endpoint Not Found</h1>
          <Link to="/" className="mt-4 inline-block text-emerald-600 hover:underline">
            Back to Home
          </Link>
        </div>
      </div>
    );
  }

  const allParams = [...endpoint.pathParams, ...endpoint.queryParams];
  const requiredParams = allParams.filter((p) => p.required);
  const optionalParams = allParams.filter((p) => !p.required);

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-emerald-600 shadow-md">
        <div className="mx-auto max-w-7xl px-4 py-6">
          <nav className="mb-2 flex gap-2 text-sm">
            <Link to="/" className="text-emerald-200 hover:text-white">
              Home
            </Link>
            <span className="text-emerald-300">/</span>
            <Link to={`/category/${categoryId}`} className="text-emerald-200 hover:text-white">
              {category.name}
            </Link>
          </nav>
          <div className="flex items-center gap-3">
            <span className="rounded bg-white/20 px-2 py-1 text-sm font-semibold text-white">
              {endpoint.method}
            </span>
            <h1 className="text-2xl font-bold text-white">{endpoint.name}</h1>
          </div>
          <code className="mt-2 block text-emerald-100">{endpoint.path}</code>
          <p className="mt-2 text-emerald-100">{endpoint.description}</p>
        </div>
      </header>

      {/* Main content */}
      <main className="mx-auto max-w-7xl px-4 py-8">
        <div className="grid gap-8 lg:grid-cols-2">
          {/* Form */}
          <div>
            <h2 className="mb-4 text-xl font-semibold text-gray-800">Parameters</h2>
            <form
              onSubmit={handleSubmit}
              className="rounded-lg border border-gray-200 bg-white p-6 shadow-sm"
            >
              {/* Required parameters */}
              {requiredParams.length > 0 && (
                <div className="mb-6">
                  <h3 className="mb-3 text-sm font-semibold uppercase tracking-wide text-gray-500">
                    Required
                  </h3>
                  <div className="space-y-4">
                    {requiredParams.map((param) => (
                      <div key={param.name}>
                        <label
                          htmlFor={param.name}
                          className="mb-1 block text-sm font-medium text-gray-700"
                        >
                          {param.name}
                          <span className="ml-1 text-red-500">*</span>
                        </label>
                        <FormField
                          param={param}
                          value={formValues[param.name] || ''}
                          onChange={(value) => handleFieldChange(param.name, value)}
                        />
                        <p className="mt-1 text-xs text-gray-500">{param.description}</p>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {/* Optional parameters */}
              {optionalParams.length > 0 && (
                <div className="mb-6">
                  <h3 className="mb-3 text-sm font-semibold uppercase tracking-wide text-gray-500">
                    Optional
                  </h3>
                  <div className="space-y-4">
                    {optionalParams.map((param) => (
                      <div key={param.name}>
                        {param.type !== 'boolean' && (
                          <label
                            htmlFor={param.name}
                            className="mb-1 block text-sm font-medium text-gray-700"
                          >
                            {param.name}
                          </label>
                        )}
                        <FormField
                          param={param}
                          value={formValues[param.name] || ''}
                          onChange={(value) => handleFieldChange(param.name, value)}
                        />
                        {param.type !== 'boolean' && (
                          <p className="mt-1 text-xs text-gray-500">{param.description}</p>
                        )}
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {allParams.length === 0 && (
                <p className="mb-6 text-gray-500">This endpoint has no parameters.</p>
              )}

              <button
                type="submit"
                disabled={loading}
                className="w-full rounded-lg bg-emerald-600 px-4 py-3 font-semibold text-white transition-colors hover:bg-emerald-700 disabled:bg-gray-400"
              >
                {loading ? 'Loading...' : 'Send Request'}
              </button>
            </form>
          </div>

          {/* Results */}
          <div>
            <h2 className="mb-4 text-xl font-semibold text-gray-800">Response</h2>

            {/* Request URL */}
            {requestUrl && (
              <div className="mb-4 rounded-lg border border-gray-200 bg-white p-4 shadow-sm">
                <h3 className="mb-1 text-sm font-semibold text-gray-500">Request URL</h3>
                <code className="block break-all rounded bg-gray-100 p-2 text-sm text-gray-700">
                  {requestUrl}
                </code>
              </div>
            )}

            {/* Loading */}
            {loading && (
              <div className="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
                <div className="flex items-center justify-center py-12">
                  <div className="h-8 w-8 animate-spin rounded-full border-4 border-emerald-500 border-t-transparent"></div>
                  <span className="ml-3 text-gray-600">Fetching data...</span>
                </div>
              </div>
            )}

            {/* Error */}
            {error && (
              <div className="rounded-lg border border-red-200 bg-red-50 p-4 text-red-700">
                <p className="font-medium">Error</p>
                <p className="text-sm">{error}</p>
              </div>
            )}

            {/* Result with table/JSON toggle and export */}
            {result !== null && !loading && (
              <ResultDisplay data={result} endpointId={endpoint.id} />
            )}

            {/* Empty state */}
            {!loading && !error && result === null && (
              <div className="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
                <p className="py-12 text-center text-gray-400">
                  Fill out the form and click "Send Request" to see results
                </p>
              </div>
            )}
          </div>
        </div>
      </main>
    </div>
  );
}
