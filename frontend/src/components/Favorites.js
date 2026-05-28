import ProductCard from "./ProductCard";

function Favorites({favorites,setFavorites, compareItems, setCompareItems}) {
    return (
         <div className="p-4">
             <h1 className="text-2xl font-bold mb-6 "> Favorite </h1>

            {favorites.length===0 ? (
                <p className="text-gray-500 text-center text-lg">Nu există produse favorite.</p>
            ) : (
                <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                    {favorites.map(item=>(
                        <ProductCard
                            key={item.id}
                            offer={item}
                            favorites={favorites}
                            setFavorites={setFavorites}
                            compareItems={compareItems}
                            setCompareItems={setCompareItems}
                        />
                    ))}
                </div>
            )}
        </div>
    );
}

export default Favorites;