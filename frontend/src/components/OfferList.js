import React, { useEffect, useState } from 'react';
import axios from 'axios'; // Instalăm axios pentru cereri HTTP
import ProductCard from './ProductCard';
import SearchBar from './SearchBar';

function OfferList({favorites, setFavorites, showFavorites}) {
    const [offers, setOffers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [page, setPage] = useState(0);
    const [compareItems,setCompareItems]=useState([]);

    /*const [favorites, setFavorites] = useState(()=>{
        const saved=localStorage.getItem("favorites");
        return saved? JSON.parse(saved):[];
    });*/
    const [searchTerm, setSearchTerm] = useState('');
    useEffect(() => {
        localStorage.setItem("favorites", JSON.stringify(favorites));
    },[favorites]);
    useEffect(() => {
        const fetchOffers = async () => {
            try {
                setLoading(true);
                setError(null);
                //const API_URL=process.env.REACT_APP_API_URL;
                // Apelul către endpoint-ul backend-ului nostru Java
                const response = await axios.get(`/api/offers?page=${page}&size=12&keyword=${searchTerm}`);//192.168.1.131
                setOffers(response.data);
            } catch (err) {
                setError("Nu s-au putut încărca produsele");
                console.error("Eroare la preluarea ofertelor:", err);
            } finally {
                setLoading(false);
            }
        };
        fetchOffers();
    }, [page,searchTerm]); // Re-rulează când searchTerm se schimbă
    const displayedOffers =
        showFavorites
            ? favorites
            : offers;
    return (

        <div className="px-4">

            <SearchBar
                searchTerm={searchTerm}
                setSearchTerm={setSearchTerm}
                setPage={setPage}
            />
            {loading && (
                <p className="text-center text-xl">
                    Loading...
                </p>
            )}
            {error &&<p className="text-red-500">{error}</p>}
            <div className="
                grid
                grid-cols-1
                sm:grid-cols-2
                md:grid-cols-3
                lg:grid-cols-4
                gap-6
            ">

                {displayedOffers.map((offer) => (

                    <ProductCard
                        key={offer.id}
                        offer={offer}
                        favorites={favorites}
                        setFavorites={setFavorites}
                        compareItems={compareItems}
                        setCompareItems={setCompareItems}
                    />

                ))}

            </div>
            <div className="flex
                justify-center
                items-center
                gap-4
                mt-8
                mb-6">

                <button
                    onClick={() => setPage(prev => Math.max(prev - 1, 0))}
                    className="px-5
                        py-3
                        rounded-xl
                        bg-gray-200"
                >
                    Previous
                </button>
                <span className="font-semibold">
                    Pagina {page + 1}
                </span>
                <button
                    onClick={() => setPage(prev => prev + 1)}
                    className="px-5
                        py-3
                        rounded-xl
                        bg-blue-500
                        text-white"
                >
                    Next
                </button>

            </div>


        </div>
    );
}
export default OfferList;