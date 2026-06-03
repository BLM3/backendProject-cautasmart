import React, { useState } from 'react';
function CompareBar({ compareItems=[], setCompareItems }) {
    const [isOpen, setIsOpen] = useState(false); // Stare pentru deschiderea tabelului comparativ
    if(!compareItems.length){
        return null;
    }
    const removeItem = (id) => {
        setCompareItems(compareItems.filter(item => item.id !== id));
    };
    return (
        <>
            {/* BARA DE JOS FIXĂ */}
            <div className="fixed bottom-0 left-0 right-0 bg-white dark:bg-gray-900 shadow-[0_-5px_15px_rgba(0,0,0,0.1)] dark:shadow-[0_-5px_15px_rgba(0,0,0,0.5)] p-3 border-t border-gray-200 dark:border-gray-800 z-40 flex flex-row justify-between items-center gap-2 transition-colors duration-300">
                <div className="flex items-center gap-2 overflow-x-auto no-scrollbar">
                    <span className="font-bold text-xs sm:text-sm text-gray-700 dark:text-gray-300 whitespace-nowrap">Comparație ({compareItems.length}/3):</span>
                    <div className="flex gap-1.5">
                        {compareItems.map(item => (
                            <div key={item.id} className="flex items-center gap-1 bg-blue-50 dark:bg-blue-950/40 text-blue-700 dark:text-blue-300 px-2 py-1 rounded-lg text-[10px] sm:text-sm font-medium border border-blue-200 dark:border-blue-800 whitespace-nowrap">
                                <span className="max-w-[70px] sm:max-w-[120px] truncate">{item.name}</span>
                                <button onClick={() => removeItem(item.id)} className="text-red-500 hover:text-red-700 font-bold ml-0.5">X</button>
                            </div>
                        ))}
                    </div>
                </div>

                <button
                    onClick={() => setIsOpen(true)}
                    className="bg-blue-600 hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600 text-white font-semibold px-3 sm:px-6 py-2 rounded-lg sm:rounded-xl transition shadow-md text-xs sm:text-sm whitespace-nowrap"
                >
                    Compară acum ➔
                </button>
            </div>

            {/* MODALUL CU TABELUL COMPARATIV */}
            {isOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center p-4">
                    <div className="bg-white dark:bg-gray-800 rounded-2xl max-w-5xl w-full max-h-[90vh] overflow-y-auto p-6 relative shadow-2xl">

                        {/* Buton Închidere */}
                        <button
                            onClick={() => setIsOpen(false)}
                            className="absolute top-4 right-4 text-gray-400 hover:text-gray-600 dark:hover:text-gray-200 text-2xl font-bold p-2"
                        >
                            ✕
                        </button>

                        <h2 className="text-2xl font-bold text-gray-800 dark:text-gray-100 mb-6 border-b dark:border-gray-700 pb-3">Comparație Detaliată</h2>

                        {/* Structura Tabelului */}
                        <div className="w-full border border-gray-100 dark:border-gray-700 rounded-xl bg-white dark:bg-gray-900/20 overflow-x-auto md:overflow-x-hidden">
                            <table className="w-full text-left border-collapse table-fixed min-w-[600px] md:min-w-full">
                                <thead>
                                <tr className="bg-gray-50 dark:bg-gray-900/50 border-b dark:border-gray-700">
                                    {/* Prima coloană ocupă 20% din spațiu */}
                                    <th className="p-4 text-gray-500 dark:text-gray-400 font-bold text-xs sm:text-sm uppercase tracking-wider w-[20%]">
                                        Specificație
                                    </th>
                                    {compareItems.map(item => (
                                        <th key={item.id} className="p-4 font-bold text-gray-800 dark:text-gray-100 text-center">
                                            <div className="flex flex-col items-center gap-2">
                                                <div className="w-14 h-14 bg-white rounded-lg p-1 border dark:border-gray-700 flex items-center justify-center overflow-hidden shadow-sm">
                                                    <img src={item.imageUrl} alt={item.name} className="max-w-full max-h-full object-contain" />
                                                </div>
                                                <span className="text-xs sm:text-sm line-clamp-1 leading-tight font-bold px-2">{item.name}</span>
                                            </div>
                                        </th>
                                    ))}
                                </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-100 dark:divide-gray-700">
                                {/* Rând Preț */}
                                <tr className="hover:bg-gray-50/50 dark:hover:bg-gray-800/30 transition">
                                    <td className="p-4 font-bold text-gray-700 dark:text-gray-300 text-xs sm:text-sm">Preț</td>
                                    {compareItems.map(item => (
                                        <td key={item.id} className="p-4 text-center font-black text-green-600 dark:text-green-400 text-base">
                                            {item.price} <span className="text-xs font-bold">{item.currency}</span>
                                        </td>
                                    ))}
                                </tr>
                                {/* Rând Rating */}
                                <tr className="hover:bg-gray-50/50 dark:hover:bg-gray-800/30 transition">
                                    <td className="p-4 font-bold text-gray-700 dark:text-gray-300 text-xs sm:text-sm">Rating</td>
                                    {compareItems.map(item => (
                                        <td key={item.id} className="p-4 text-center text-amber-500 font-bold text-sm">
                                            ⭐ {item.rating} / 5
                                        </td>
                                    ))}
                                </tr>
                                {/* Rând Categorie */}
                                <tr className="hover:bg-gray-50/50 dark:hover:bg-gray-800/30 transition">
                                    <td className="p-4 font-bold text-gray-700 dark:text-gray-300 text-xs sm:text-sm">Categorie</td>
                                    {compareItems.map(item => (
                                        <td key={item.id} className="p-4 text-center text-gray-500 dark:text-gray-400 text-xs font-medium">
                                            <span className="bg-gray-100 dark:bg-gray-800 px-2 py-1 rounded-md">{item.category}</span>
                                        </td>
                                    ))}
                                </tr>
                                {/* Rând Descriere */}
                                <tr className="hover:bg-gray-50/50 dark:hover:bg-gray-800/30 transition">
                                    <td className="p-4 font-bold text-gray-700 dark:text-gray-300 text-xs sm:text-sm">Descriere</td>
                                    {compareItems.map(item => (
                                        <td key={item.id} className="p-4 text-gray-500 dark:text-gray-400 text-xs text-center px-2 leading-relaxed">
                                            <div className="line-clamp-2" title={item.description}>
                                                {item.description || "Nicio descriere disponibilă."}
                                            </div>
                                        </td>
                                    ))}
                                </tr>
                                {/* Rând Acțiune */}
                                <tr className="hover:bg-gray-50/50 dark:hover:bg-gray-800/30 transition">
                                    <td className="p-4 font-bold text-gray-700 dark:text-gray-300 text-xs sm:text-sm">Acțiune</td>
                                    {compareItems.map(item => (
                                        <td key={item.id} className="p-4 text-center">
                                            <a
                                                href={item.affiliateLink}
                                                target="_blank"
                                                rel="noopener noreferrer"
                                                className="inline-block bg-green-500 hover:bg-green-600 dark:bg-green-600 dark:hover:bg-green-700 text-white text-xs font-bold px-4 py-2 rounded-xl transition shadow-sm whitespace-nowrap"
                                            >
                                                Cumpără ➔
                                            </a>
                                        </td>
                                    ))}
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
}

export default CompareBar;