import React from 'react';

function FilterBar({ category, setCategory, sortBy, setSortBy, setPage }) {
    // Listă statică de categorii (le poți schimba în funcție de ce ai în JSON)
    const categories = ["All", "Electronice", "Accesorii", "Audio", "Auto", "IPhone"];

    return (
        <div className="flex flex-col sm:flex-row justify-between items-center gap-4 bg-white dark:bg-gray-800 p-4 rounded-xl shadow-sm mb-6 mx-4">
            {/* Dropdown Categorii */}
            <div className="flex items-center gap-2 w-full sm:w-auto">
                <span className="text-sm font-semibold text-gray-600 whitespace-nowrap">Categorie:</span>
                <select
                    value={category}
                    onChange={(e) => { setCategory(e.target.value); setPage(0); }}
                    className="w-full sm:w-auto p-2 border rounded-lg bg-gray-50 dark:bg-gray-700 text-gray-700 dark:text-gray-200 border-gray-200 dark:border-gray-600 font-medium focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                    {categories.map((cat, idx) => (
                        <option key={idx} value={cat}>{cat}</option>
                    ))}
                </select>
            </div>

            {/* Dropdown Sortare */}
            <div className="flex items-center gap-2 w-full sm:w-auto">
                <span className="text-sm font-semibold text-gray-600 whitespace-nowrap">Sortează după:</span>
                <select
                    value={sortBy}
                    onChange={(e) => { setSortBy(e.target.value); setPage(0); }}
                    className="w-full sm:w-auto p-2 border rounded-lg bg-gray-50 dark:bg-gray-700 text-gray-700 dark:text-gray-200 border-gray-200 dark:border-gray-600 font-medium focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                    <option value="">Implicit</option>
                    <option value="price_asc">Preț: Mic ➔ Mare</option>
                    <option value="price_desc">Preț: Mare ➔ Mic</option>
                    <option value="rating_desc">Cele mai apreciate (⭐)</option>
                </select>
            </div>
        </div>
    );
}

export default FilterBar;