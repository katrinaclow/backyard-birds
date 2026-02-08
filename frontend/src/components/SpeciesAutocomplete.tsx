import { useState, useEffect, useRef, useCallback } from 'react';
import { searchSpecies } from '../api/search';
import type { SpeciesSearchResult } from '../types/search';

interface SpeciesAutocompleteProps {
  value: string;
  onChange: (speciesCode: string) => void;
  placeholder?: string;
  required?: boolean;
}

export function SpeciesAutocomplete({
  value,
  onChange,
  placeholder = 'Search by common name or species code...',
  required = false,
}: SpeciesAutocompleteProps) {
  const [inputValue, setInputValue] = useState(value);
  const [results, setResults] = useState<SpeciesSearchResult[]>([]);
  const [isOpen, setIsOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [selectedDisplay, setSelectedDisplay] = useState<string>('');
  const wrapperRef = useRef<HTMLDivElement>(null);
  const debounceRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  // Close dropdown when clicking outside
  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (wrapperRef.current && !wrapperRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  // Debounced search
  const handleSearch = useCallback(async (query: string) => {
    if (query.length < 2) {
      setResults([]);
      return;
    }

    setLoading(true);
    try {
      const data = await searchSpecies(query);
      setResults(data);
      setIsOpen(true);
    } catch {
      setResults([]);
    } finally {
      setLoading(false);
    }
  }, []);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = e.target.value;
    setInputValue(newValue);
    setSelectedDisplay('');

    // Clear previous timeout
    if (debounceRef.current) {
      clearTimeout(debounceRef.current);
    }

    // Debounce search by 300ms
    debounceRef.current = setTimeout(() => {
      handleSearch(newValue);
    }, 300);
  };

  const handleSelect = (species: SpeciesSearchResult) => {
    setInputValue(species.speciesCode);
    setSelectedDisplay(`${species.commonName} (${species.speciesCode})`);
    onChange(species.speciesCode);
    setIsOpen(false);
    setResults([]);
  };

  const handleClear = () => {
    setInputValue('');
    setSelectedDisplay('');
    onChange('');
    setResults([]);
  };

  return (
    <div ref={wrapperRef} className="relative">
      <div className="relative">
        <input
          type="text"
          value={selectedDisplay || inputValue}
          onChange={handleInputChange}
          onFocus={() => results.length > 0 && setIsOpen(true)}
          placeholder={placeholder}
          required={required}
          className="w-full rounded-lg border border-gray-300 px-3 py-2 pr-8 focus:border-emerald-500 focus:ring-2 focus:ring-emerald-500 focus:outline-none"
          aria-label="Species search"
          aria-autocomplete="list"
          aria-expanded={isOpen}
        />
        {(inputValue || selectedDisplay) && (
          <button
            type="button"
            onClick={handleClear}
            className="absolute right-2 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
            aria-label="Clear selection"
          >
            ✕
          </button>
        )}
      </div>

      {loading && (
        <div className="absolute right-10 top-1/2 -translate-y-1/2">
          <div className="h-4 w-4 animate-spin rounded-full border-2 border-emerald-500 border-t-transparent"></div>
        </div>
      )}

      {isOpen && results.length > 0 && (
        <ul
          className="absolute z-50 mt-1 max-h-60 w-full overflow-auto rounded-lg border border-gray-200 bg-white shadow-lg"
          role="listbox"
        >
          {results.map((species) => (
            <li
              key={species.speciesCode}
              onClick={() => handleSelect(species)}
              className="cursor-pointer px-3 py-2 hover:bg-emerald-50"
              role="option"
              aria-selected={inputValue === species.speciesCode}
            >
              <div className="font-medium text-gray-900">{species.commonName}</div>
              <div className="text-sm text-gray-500">
                <span className="italic">{species.scientificName}</span>
                <span className="ml-2 rounded bg-gray-100 px-1 text-xs">
                  {species.speciesCode}
                </span>
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
