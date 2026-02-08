import { useState, useMemo } from 'react';

interface ResultDisplayProps {
  data: unknown;
  endpointId: string;
}

type ViewMode = 'table' | 'json';

// Convert data to CSV string
function toCSV(data: unknown): string {
  if (!Array.isArray(data) || data.length === 0) {
    // For non-array or empty data, just stringify
    return JSON.stringify(data, null, 2);
  }

  // Get all unique keys from all objects
  const allKeys = new Set<string>();
  data.forEach((item) => {
    if (typeof item === 'object' && item !== null) {
      Object.keys(item).forEach((key) => allKeys.add(key));
    }
  });
  const headers = Array.from(allKeys);

  // Create CSV rows
  const rows = data.map((item) => {
    return headers
      .map((header) => {
        const value = (item as Record<string, unknown>)[header];
        // Handle different types
        if (value === null || value === undefined) return '';
        if (typeof value === 'object') return JSON.stringify(value);
        // Escape quotes and wrap in quotes if contains comma or newline
        const str = String(value);
        if (str.includes(',') || str.includes('\n') || str.includes('"')) {
          return `"${str.replace(/"/g, '""')}"`;
        }
        return str;
      })
      .join(',');
  });

  return [headers.join(','), ...rows].join('\n');
}

// Download data as file
function downloadFile(content: string, filename: string, mimeType: string) {
  const blob = new Blob([content], { type: mimeType });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
}

// Generic table for array data
function DataTable({ data }: { data: Record<string, unknown>[] }) {
  if (data.length === 0) {
    return <p className="text-gray-500">No data to display</p>;
  }

  // Get headers from first item
  const headers = Object.keys(data[0]);

  // Format cell value for display
  const formatCell = (value: unknown): string => {
    if (value === null || value === undefined) return '-';
    if (typeof value === 'boolean') return value ? 'Yes' : 'No';
    if (typeof value === 'object') return JSON.stringify(value);
    return String(value);
  };

  return (
    <div className="overflow-x-auto">
      <table className="min-w-full divide-y divide-gray-200">
        <thead className="bg-gray-50">
          <tr>
            {headers.map((header) => (
              <th
                key={header}
                className="px-4 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500"
              >
                {header.replace(/([A-Z])/g, ' $1').trim()}
              </th>
            ))}
          </tr>
        </thead>
        <tbody className="divide-y divide-gray-200 bg-white">
          {data.map((row, index) => (
            <tr key={index} className="hover:bg-gray-50">
              {headers.map((header) => (
                <td key={header} className="whitespace-nowrap px-4 py-3 text-sm text-gray-700">
                  {formatCell(row[header])}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

// JSON viewer
function JsonViewer({ data }: { data: unknown }) {
  const jsonString = JSON.stringify(data, null, 2);
  const lineCount = jsonString.split('\n').length;

  return (
    <div className="relative">
      <pre className="max-h-[600px] overflow-auto rounded-lg bg-gray-900 p-4 text-sm text-gray-100">
        <code>{jsonString}</code>
      </pre>
      <div className="absolute right-2 top-2 rounded bg-gray-700 px-2 py-1 text-xs text-gray-300">
        {lineCount} lines
      </div>
    </div>
  );
}

export function ResultDisplay({ data, endpointId }: ResultDisplayProps) {
  const [viewMode, setViewMode] = useState<ViewMode>('table');

  const isArrayData = Array.isArray(data);
  const arrayData = isArrayData ? (data as Record<string, unknown>[]) : null;

  const csvContent = useMemo(() => toCSV(data), [data]);

  const handleExportCSV = () => {
    const filename = `${endpointId}-${new Date().toISOString().split('T')[0]}.csv`;
    downloadFile(csvContent, filename, 'text/csv;charset=utf-8;');
  };

  const handleExportJSON = () => {
    const filename = `${endpointId}-${new Date().toISOString().split('T')[0]}.json`;
    downloadFile(JSON.stringify(data, null, 2), filename, 'application/json');
  };

  return (
    <div>
      {/* Controls */}
      <div className="mb-4 flex flex-wrap items-center justify-between gap-4">
        <div className="flex items-center gap-2">
          {/* View mode toggle */}
          <div className="inline-flex rounded-lg border border-gray-200 bg-white p-1">
            <button
              onClick={() => setViewMode('table')}
              className={`rounded-md px-3 py-1.5 text-sm font-medium transition-colors ${
                viewMode === 'table'
                  ? 'bg-emerald-100 text-emerald-700'
                  : 'text-gray-600 hover:bg-gray-100'
              }`}
              disabled={!isArrayData}
              title={!isArrayData ? 'Table view only available for array data' : undefined}
            >
              Table
            </button>
            <button
              onClick={() => setViewMode('json')}
              className={`rounded-md px-3 py-1.5 text-sm font-medium transition-colors ${
                viewMode === 'json'
                  ? 'bg-emerald-100 text-emerald-700'
                  : 'text-gray-600 hover:bg-gray-100'
              }`}
            >
              JSON
            </button>
          </div>

          {isArrayData && (
            <span className="text-sm text-gray-500">{arrayData?.length} items</span>
          )}
        </div>

        {/* Export buttons */}
        <div className="flex gap-2">
          <button
            onClick={handleExportCSV}
            className="inline-flex items-center gap-1 rounded-lg border border-gray-300 bg-white px-3 py-1.5 text-sm font-medium text-gray-700 transition-colors hover:bg-gray-50"
          >
            <span>Export CSV</span>
          </button>
          <button
            onClick={handleExportJSON}
            className="inline-flex items-center gap-1 rounded-lg border border-gray-300 bg-white px-3 py-1.5 text-sm font-medium text-gray-700 transition-colors hover:bg-gray-50"
          >
            <span>Export JSON</span>
          </button>
        </div>
      </div>

      {/* Content */}
      <div className="rounded-lg border border-gray-200 bg-white">
        {viewMode === 'table' && isArrayData && arrayData ? (
          <DataTable data={arrayData} />
        ) : (
          <div className="p-4">
            <JsonViewer data={data} />
          </div>
        )}
      </div>
    </div>
  );
}
