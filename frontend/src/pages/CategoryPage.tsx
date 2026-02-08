import { Link, useParams } from 'react-router-dom';
import { getCategoryById } from '../data/endpoints';

export function CategoryPage() {
  const { categoryId } = useParams<{ categoryId: string }>();
  const category = categoryId ? getCategoryById(categoryId) : undefined;

  if (!category) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-gray-50">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-gray-900">Category Not Found</h1>
          <Link to="/" className="mt-4 inline-block text-emerald-600 hover:underline">
            Back to Home
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-emerald-600 shadow-md">
        <div className="mx-auto max-w-7xl px-4 py-6">
          <nav className="mb-2">
            <Link to="/" className="text-emerald-200 hover:text-white">
              ← Back to Categories
            </Link>
          </nav>
          <div className="flex items-center gap-4">
            <span className="text-4xl">{category.icon}</span>
            <div>
              <h1 className="text-3xl font-bold text-white">{category.name}</h1>
              <p className="mt-1 text-emerald-100">{category.description}</p>
            </div>
          </div>
        </div>
      </header>

      {/* Main content */}
      <main className="mx-auto max-w-7xl px-4 py-8">
        <h2 className="mb-6 text-xl font-semibold text-gray-800">Available Endpoints</h2>

        <div className="space-y-4">
          {category.endpoints.map((endpoint) => (
            <Link
              key={endpoint.id}
              to={`/category/${categoryId}/endpoint/${endpoint.id}`}
              className="group block rounded-lg border border-gray-200 bg-white p-5 shadow-sm transition-all hover:border-emerald-300 hover:shadow-md"
            >
              <div className="flex items-start justify-between">
                <div className="flex-1">
                  <div className="mb-2 flex items-center gap-3">
                    <span className="rounded bg-emerald-100 px-2 py-1 text-xs font-semibold text-emerald-700">
                      {endpoint.method}
                    </span>
                    <code className="text-sm text-gray-600">{endpoint.path}</code>
                  </div>
                  <h3 className="text-lg font-semibold text-gray-900 group-hover:text-emerald-600">
                    {endpoint.name}
                  </h3>
                  <p className="mt-1 text-gray-600">{endpoint.description}</p>
                </div>
                <span className="text-gray-400 group-hover:text-emerald-600">→</span>
              </div>

              {/* Parameter summary */}
              <div className="mt-3 flex gap-4 text-sm text-gray-500">
                {endpoint.pathParams.length > 0 && (
                  <span>
                    {endpoint.pathParams.length} path param{endpoint.pathParams.length !== 1 ? 's' : ''}
                  </span>
                )}
                {endpoint.queryParams.length > 0 && (
                  <span>
                    {endpoint.queryParams.length} query param{endpoint.queryParams.length !== 1 ? 's' : ''}
                  </span>
                )}
                {endpoint.pathParams.length === 0 && endpoint.queryParams.length === 0 && (
                  <span>No parameters</span>
                )}
              </div>
            </Link>
          ))}
        </div>
      </main>
    </div>
  );
}
