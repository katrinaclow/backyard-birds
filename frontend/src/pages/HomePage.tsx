import { Link } from 'react-router-dom';
import { categories } from '../data/endpoints';

export function HomePage() {
  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-emerald-600 shadow-md">
        <div className="mx-auto max-w-7xl px-4 py-8">
          <h1 className="text-4xl font-bold text-white">Backyard Birds API Explorer</h1>
          <p className="mt-2 text-lg text-emerald-100">
            Explore the eBird API through an intuitive interface
          </p>
        </div>
      </header>

      {/* Main content */}
      <main className="mx-auto max-w-7xl px-4 py-12">
        <h2 className="mb-8 text-2xl font-semibold text-gray-800">API Categories</h2>

        <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {categories.map((category) => (
            <Link
              key={category.id}
              to={`/category/${category.id}`}
              className="group rounded-xl border border-gray-200 bg-white p-6 shadow-sm transition-all hover:border-emerald-300 hover:shadow-md"
            >
              <div className="mb-4 text-4xl">{category.icon}</div>
              <h3 className="mb-2 text-xl font-semibold text-gray-900 group-hover:text-emerald-600">
                {category.name}
              </h3>
              <p className="mb-4 text-gray-600">{category.description}</p>
              <p className="text-sm text-gray-400">
                {category.endpoints.length} endpoint{category.endpoints.length !== 1 ? 's' : ''}
              </p>
            </Link>
          ))}
        </div>
      </main>

      {/* Footer */}
      <footer className="mt-12 border-t border-gray-200 bg-white py-8">
        <div className="mx-auto max-w-7xl px-4 text-center text-gray-500">
          <p>Powered by the eBird API</p>
        </div>
      </footer>
    </div>
  );
}
