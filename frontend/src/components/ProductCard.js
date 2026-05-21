function ProductCard({ offer,favorites=[],setFavorites,compareItems=[],setCompareItems }) {
    const isFavorite=favorites.some(item=>item.id===offer.id);
    const isCompared=compareItems.some(item=>item.id===offer.id);
    const toggleFavorite=()=>{
        if(isFavorite){
            setFavorites(favorites.filter(item=>item.id !==offer.id));
        }else{
            setFavorites([...favorites,offer]);
        }
    }
    const toggleCompare=()=>{
        if(isCompared){
            setCompareItems(compareItems.filter(item=>item.id !==offer.id));
        }else if(compareItems.length<3){
            setCompareItems([...compareItems,offer]);
        }
    }
    return (

        <div className="
            bg-white
            rounded-2xl
            shadow-md
            hover:shadow-xl
            transition-all
            duration-300
            overflow-hidden
            flex
            flex-col
        ">
            {/*favorites*/}
            <div className="
                flex
                gap-2
                mt-4
            ">
                <button onClick={toggleFavorite}
                        className="
                        flex-1
                        p-2
                        rounded
                        bg-red-500
                        text-white
                ">
                    {isFavorite ? "❤️" : "🤍"}
                </button>
                <button
                    onClick={toggleCompare}
                    className="
                        flex-1
                        p-2
                        rounded
                        bg-blue-500
                        text-white
                    "
                >
                    {isCompared
                        ? "Selectat"
                        : "Compară"}
                </button>
            </div>
            {/* IMAGE CONTAINER FIX */}
            <div className="w-full h-56 bg-gray-100 overflow-hidden">
                <img
                    src={offer.imageUrl}
                    alt={offer.name}
                    className="w-full
                            h-full
                            object-cover
                            hover:scale-110
                            transition-transform
                            duration-500"
                />
            </div>
            {/* CONTENT */}
            <div className="p-5 flex flex-col flex-1">

                <h2 className="text-lg font-bold text-gray-800 line-clamp-2">
                    {offer.name}
                </h2>

                <p className="text-sm text-gray-500 mt-2 line-clamp-3">
                    {offer.description}
                </p>
                {/* PRICE + RATING */}
                <div className="flex justify-between items-center mt-4">

                    <span className="text-xl font-bold text-green-600">
                        {offer.price} {offer.currency}
                    </span>

                    <span className="text-yellow-500 font-medium">
                        ⭐ {offer.rating}
                    </span>

                </div>
                {/* CATEGORY */}
                <p className="text-xs text-gray-400 mt-1">
                    {offer.category}
                </p>
                {/* BUTTON */}
                <a
                    href={offer.affiliateLink}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="
                        mt-auto
                        text-center
                        bg-blue-600
                        hover:bg-blue-700
                        text-white
                        py-2.5
                        rounded-xl
                        font-semibold
                        transition
                    "
                >
                    Vezi oferta
                </a>

            </div>

        </div>
    );
}

export default ProductCard;