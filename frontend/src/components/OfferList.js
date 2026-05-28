import React, { useEffect, useState } from 'react';
import axios from 'axios'; // Instalăm axios pentru cereri HTTP
import ProductCard from './ProductCard';
import SearchBar from './SearchBar';

function OfferList({favorites, setFavorites, showFavorites, compareItems, setCompareItems}) {
    const [offers, setOffers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [page, setPage] = useState(0);
    const [searchTerm, setSearchTerm] = useState('');
    // Starea nouă pentru valoarea cu întârziere
    const [debouncedSearchTerm, setDebouncedSearchTerm] = useState('');

    // 1. Efect pentru DEBOUNCE: Așteaptă 500ms înainte de a schimba debouncedSearchTerm
    useEffect(() => {
        const handler = setTimeout(() => {
            setDebouncedSearchTerm(searchTerm);
        }, 500);

        // Curățăm timer-ul dacă utilizatorul tastează din nou înainte de expirarea celor 500ms
        return () => {
            clearTimeout(handler);
        };
    }, [searchTerm]);
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
                const response = await axios.get(`/api/offers?page=${page}&size=12&keyword=${debouncedSearchTerm}`);//192.168.1.131
                setOffers(response.data);
            } catch (err) {
                setError("Nu s-au putut încărca produsele");
                console.error("Eroare la preluarea ofertelor:", err);
            } finally {
                setLoading(false);
            }
        };
        fetchOffers();
    }, [page,debouncedSearchTerm]); // Re-rulează când searchTerm se schimbă

    return (

        <div className="px-4">

            <SearchBar
                searchTerm={searchTerm}
                setSearchTerm={setSearchTerm}
                setPage={setPage}
            />
            {loading && (<p className="text-center text-xl mt-4">Se incarca produsele...</p>)}
            {error &&<p className="text-red-500 text-center mt-4">{error}</p>}

            {!loading && !error && offers.length === 0 && (
                <p className="text-center text-gray-500 mt-4">Nu s-au găsit produse pentru căutarea ta.</p>
            )}

            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                {offers.map((offer) => (
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
            {/* Paginare */}

            <div className="flex justify-center items-center gap-4 mt-8 mb-6">
                <button
                    disabled={page === 0}
                    onClick={() => setPage(prev => Math.max(prev - 1, 0))}
                    className={`px-5 py-3 rounded-xl font-medium ${page === 0 ? 'bg-gray-300 text-gray-500 cursor-not-allowed' : 'bg-gray-200 hover:bg-gray-300'}`}>
                    Previous
                </button>
                <span className="font-semibold">
                    Pagina {page + 1}
                </span>
                <button
                    onClick={() => setPage(prev => prev + 1)}
                    className="px-5 py-3 rounded-xl bg-blue-500 hover:bg-blue-600 text-white font-medium">
                    Next
                </button>

            </div>


        </div>
    );
}
export default OfferList;