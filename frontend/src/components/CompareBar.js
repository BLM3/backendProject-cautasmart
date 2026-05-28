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
            <div className="fixed bottom-0 left-0 right-0 bg-white shadow-[0_-5px_15px_rgba(0,0,0,0.1)] p-4 border-t z-40 flex flex-col sm:flex-row justify-between items-center gap-4">
                <div className="flex items-center gap-4 overflow-x-auto w-full sm:w-auto">
                    <span className="font-bold text-gray-700 whitespace-nowrap">Comparație ({compareItems.length}/3):</span>
                    <div className="flex gap-2">
                        {compareItems.map(item => (
                            <div key={item.id} className="flex items-center gap-2 bg-blue-50 text-blue-700 px-3 py-1.5 rounded-lg text-sm font-medium border border-blue-200 whitespace-nowrap">
                                <span className="max-w-[120px] truncate">{item.name}</span>
                                <button onClick={() => removeItem(item.id)} className="text-red-500 hover:text-red-700 font-bold ml-1">✕</button>
                            </div>
                        ))}
                    </div>
                </div>

                <button
                    onClick={() => setIsOpen(true)}
                    className="w-full sm:w-auto bg-blue-600 hover:bg-blue-700 text-white font-semibold px-6 py-2.5 rounded-xl transition shadow-md"
                >
                    Compară produsele acum ➔
                </button>
            </div>

            {/* MODALUL CU TABELUL COMPARATIV */}
            {isOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center p-4">
                    <div className="bg-white rounded-2xl max-w-5xl w-full max-h-[90vh] overflow-y-auto p-6 relative shadow-2xl">

                        {/* Buton Închidere */}
                        <button
                            onClick={() => setIsOpen(false)}
                            className="absolute top-4 right-4 text-gray-400 hover:text-gray-600 text-2xl font-bold p-2"
                        >
                            ✕
                        </button>

                        <h2 className="text-2xl font-bold text-gray-800 mb-6 border-b pb-3">Comparație Detaliată</h2>

                        {/* Structura Tabelului */}
                        <div className="overflow-x-auto">
                            <table className="w-full text-left border-collapse">
                                border-b
                                <thead>
                                <tr className="bg-gray-50">
                                    <th className="p-3 text-gray-500 font-medium border-b w-1/4">Specificație</th>
                                    {compareItems.map(item => (
                                        <th key={item.id} className="p-3 font-bold text-gray-800 border-b text-center w-1/3">
                                            <div className="flex flex-col items-center gap-2">
                                                <img src={item.imageUrl} alt={item.name} className="w-20 h-20 object-cover rounded-lg" />
                                                <span className="text-sm line-clamp-2 max-w-[180px]">{item.name}</span>
                                            </div>
                                        </th>
                                    ))}
                                </tr>
                                </thead>
                                <tbody>
                                {/* Rând Preț */}
                                <tr className="hover:bg-gray-50 transition border-b">
                                    <td className="p-4 font-semibold text-gray-600">Preț</td>
                                    {compareItems.map(item => (
                                        <td key={item.id} className="p-4 text-center font-bold text-green-600 text-lg">
                                            {item.price} {item.currency}
                                        </td>
                                    ))}
                                </tr>
                                {/* Rând Rating */}
                                <tr className="hover:bg-gray-50 transition border-b">
                                    <td className="p-4 font-semibold text-gray-600">Rating</td>
                                    {compareItems.map(item => (
                                        <td key={item.id} className="p-4 text-center text-yellow-500 font-medium">
                                            ⭐ {item.rating} / 5
                                        </td>
                                    ))}
                                </tr>
                                {/* Rând Categorie */}
                                <tr className="hover:bg-gray-50 transition border-b">
                                    <td className="p-4 font-semibold text-gray-600">Categorie</td>
                                    {compareItems.map(item => (
                                        <td key={item.id} className="p-4 text-center text-gray-600 text-sm">
                                            {item.category}
                                        </td>
                                    ))}
                                </tr>
                                {/* Rând Descriere */}
                                <tr className="hover:bg-gray-50 transition border-b">
                                    <td className="p-4 font-semibold text-gray-600">Descriere</td>
                                    {compareItems.map(item => (
                                        <td key={item.id} className="p-4 text-gray-500 text-xs text-justify max-w-[200px] leading-relaxed p-4">
                                            {item.description}
                                        </td>
                                    ))}
                                </tr>
                                {/* Rând Acțiune (Link-uri Profitshare) */}
                                <tr>
                                    <td className="p-4 font-semibold text-gray-600">Acțiune</td>
                                    {compareItems.map(item => (
                                        <td key={item.id} className="p-4 text-center">
                                            <a
                                                href={item.affiliateLink}
                                                target="_blank"
                                                rel="noopener noreferrer"
                                                className="inline-block bg-green-500 hover:bg-green-600 text-white text-sm font-semibold px-4 py-2 rounded-xl transition shadow"
                                            >
                                                Cumpără Acum ➔
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