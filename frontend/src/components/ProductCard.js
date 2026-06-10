import React from 'react';
import toast from 'react-hot-toast';
import { Heart, BarChart3 } from 'lucide-react';
function ProductCard({ offer,favorites=[],setFavorites,compareItems=[],setCompareItems,addToRecentlyViewed }) {
    const isFavorite=favorites.some(item=>item.id===offer.id);
    const isCompared=compareItems.some(item=>item.id===offer.id);
    const toggleFavorite=()=>{
        if(isFavorite){
            setFavorites(favorites.filter(item=>item.id !==offer.id));
            toast('Produs eliminat din favorite', { icon: '🗑️' });
        }else{
            setFavorites([...favorites,offer]);
            toast.success('Adăugat la favorite! ❤️');
        }
    }
    const toggleCompare=()=>{
        if(isCompared){
            setCompareItems(compareItems.filter(item=>item.id !==offer.id));
            toast('Produs eliminat din comparație', { icon: '🔄' });
        }else if(compareItems.length<5){
            setCompareItems([...compareItems,offer]);
            toast.success('Adăugat la lista de comparație! 📊');
        }else {
            toast.error('Poți compara maximum 5 produse simultan!');
        }
    }
    return (

        <div className="bg-white dark:bg-gray-800 rounded-xl sm:rounded-2xl shadow-sm hover:shadow-md transition-all duration-300 overflow-hidden flex flex-col relative border border-gray-100 dark:border-gray-700/50">
            <div className="p-2 sm:p-4 flex justify-between items-center gap-2 bg-gray-50/50 dark:bg-gray-900/20 border-b border-gray-100 dark:border-gray-700/30">
                {/* BADGE TOP RECOMANDAT*/}
                <div>
                    {offer.rating >= 4.5 ? (
                        <span className="inline-block bg-amber-500 text-white text-[13px] font-extrabold px-2 py-0.5 rounded-md shadow-sm uppercase tracking-wider">
                                ⭐ Top
                            </span>
                    ) : (<div className="w-10 h-4"></div> )
                    }
                </div>

                {/*favorites*/}
                <div className="flex items-center gap-2 sm:gap-3">
                    <button
                        onClick={toggleFavorite}
                        className={`flex h-8 w-8 sm:h-11 sm:w-11 items-center justify-center rounded-xl border transition-all duration-200 hover:scale-105 active:scale-95 ${
                            isFavorite
                                ? 'bg-red-50 dark:bg-red-950/30 border-red-200 dark:border-red-900/50 text-red-500'
                                : 'bg-white dark:bg-gray-700 border-gray-200 dark:border-gray-600 text-gray-400 hover:text-red-500 dark:hover:text-red-400'
                        }`}
                        title="Adaugă la favorite"
                    >
                        <Heart
                            size={16}
                            className={`sm:size-6 transition-transform duration-200 ${isFavorite ? 'fill-current scale-105' : ''}`}
                        />
                    </button>
                    <button
                        onClick={toggleCompare}
                        className={`flex h-8 px-2 sm:h-11 sm:px-4 items-center justify-center gap-1.5 rounded-xl text-[11px] sm:text-sm font-bold border transition-all duration-200 hover:scale-105 active:scale-95 ${
                            isCompared
                                ? 'bg-blue-600 border-blue-600 text-white shadow-sm'
                                : 'bg-gray-200 border-transparent hover:bg-gray-300 dark:bg-gray-700 dark:hover:bg-gray-600 text-gray-700 dark:text-gray-200'
                        }`}
                    >
                        <BarChart3 size={14} className="sm:size-5" />
                        <span className="hidden xs:inline">{isCompared ? "Selectat" : "Compară"}</span>
                    </button>
                </div>
            </div>
            {/* IMAGE CONTAINER FIX */}
            <div className="w-full h-32 sm:h-48 bg-gray-50 dark:bg-gray-900/50 overflow-hidden mt-1.5">
                <img
                    src={offer.imageUrl}
                    alt={offer.name}
                    className="w-full h-full object-contain p-2 hover:scale-105 transition-transform duration-500"
                />
            </div>
            {/* CONTENT */}
            <div className="p-3 sm:p-5 flex flex-col flex-1">

                <h2 className="text-xs sm:text-base font-bold text-gray-800 dark:text-gray-100 line-clamp-2 min-h-[2rem] sm:min-h-[3rem] leading-tight">
                    {offer.name}
                </h2>

                <p className="hidden sm:block text-xs text-gray-500 dark:text-gray-400 mt-1 line-clamp-2 flex-1">
                    {offer.description}
                </p>
                {/* PRICE + RATING */}
                <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-1 mt-2 sm:mt-4 w-full">

                    <div className="flex flex-col">
                        {/* Prețul vechi tăiat (apare doar dacă există o reducere reală) */}
                        {offer.oldPrice && offer.oldPrice > offer.price && (
                            <span className="text-xs sm:text-sm text-gray-400 dark:text-gray-500 line-through font-medium">
                                {offer.oldPrice} {offer.currency}
                            </span>
                        )}

                        {/* Prețul actual */}
                        <span className="text-sm sm:text-xl font-extrabold text-green-600 dark:text-green-400 flex items-center gap-2">
                            {offer.price} {offer.currency}

                            {/* Badge procent reducere calculated dinamic */}
                            {offer.oldPrice && offer.oldPrice > offer.price && (
                                <span className="text-[10px] sm:text-xs font-bold bg-red-100 text-red-600 dark:bg-red-950/40 dark:text-red-400 px-1.5 py-0.5 rounded-md animate-pulse">
                                    -{Math.round(((offer.oldPrice - offer.price) / offer.oldPrice) * 100)}%
                                </span>
                                            )}
                         </span>
                    </div>

                    {/* Rating-ul rămâne la locul lui */}
                    <span className="text-yellow-500 font-bold bg-yellow-50 dark:bg-yellow-950/30 px-1.5 py-0.5 rounded-lg text-[10px] sm:text-sm whitespace-nowrap self-end sm:self-center">
                        ⭐ {offer.rating}
                    </span>
                </div>
                {/* CATEGORY */}
                <p className="text-[10px] uppercase tracking-wider text-gray-400 mt-2 font-semibold">
                    {offer.category}
                </p>
                {/* BUTTON */}
                <a
                    href={offer.affiliateLink}
                    target="_blank"
                    rel="noopener noreferrer"
                    onClick={() => addToRecentlyViewed(offer)} // Înregistrăm vizualizarea
                    className="mt-3 text-center bg-blue-600 hover:bg-blue-700 text-white py-2 rounded-lg sm:rounded-xl text-xs sm:text-sm font-semibold transition shadow-sm block"
                >
                    Vezi oferta ➔
                </a>

            </div>

        </div>
    );
}

export default ProductCard;