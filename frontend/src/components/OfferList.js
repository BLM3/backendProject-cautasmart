import React, { useEffect, useState, useRef, useCallback } from 'react';
import axios from 'axios'; // Instalăm axios pentru cereri HTTP
import ProductCard from './ProductCard';
import SearchBar from './SearchBar';
import FilterBar from './FilterBar';
import RecentlyViewed from './RecentlyViewed';
import ProductSkeleton from './ProductSkeleton';

function OfferList({favorites, setFavorites, compareItems, setCompareItems}) {
    const [offers, setOffers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [page, setPage] = useState(0);
    const [hasMore, setHasMore] = useState(true); // Verificăm dacă mai sunt produse de adus

    const [searchTerm, setSearchTerm] = useState('');
    const [debouncedSearchTerm, setDebouncedSearchTerm] = useState('');
    const [category, setCategory]= useState('All');
    const [sortBy, setSortBy] = useState('');
    const [recentlyViewed, setRecentlyViewed] = useState(() => {
        const saved = localStorage.getItem("recentlyViewed");
        return saved ? JSON.parse(saved) : [];
    });
    // Referință pentru elementul de la finalul paginii (care declanșează scroll-ul)
    const observer = useRef();
    // Callback-ul pentru Intersection Observer
    const lastProductElementRef = useCallback(node => {
        if (loading) return;
        if (observer.current) observer.current.disconnect();

        observer.current = new IntersectionObserver(entries => {
            if (entries[0].isIntersecting && hasMore) {
                setPage(prevPage => prevPage + 1); // Trecem la pagina următoare când atingem subsolul
            }
        });

        if (node) observer.current.observe(node);
    }, [loading, hasMore]);
    // Resetăm lista și pagina la 0 când se schimbă filtrele sau căutarea
    useEffect(() => {
        setOffers([]);
        setPage(0);
        setHasMore(true);
    }, [debouncedSearchTerm, category, sortBy]);
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
                const response = await axios.get(`/api/offers?page=${page}&size=12&keyword=${debouncedSearchTerm}&category=${category}&sortBy=${sortBy}`);//192.168.1.131
                // Dacă backend-ul trimite o listă goală, înseamnă că nu mai sunt produse
                if (response.data.length === 0) {
                    setHasMore(false);
                } else {
                    // CRITIC PENTRU INFINITE SCROLL: Alipim produsele noi la cele deja existente în stare
                    setOffers(prevOffers => {
                        // Evităm duplicatele în cazul în care stările se actualizează ciudat
                        const existingIds = new Set(prevOffers.map(o => o.id));
                        const uniqueNewOffers = response.data.filter(o => !existingIds.has(o.id));
                        return [...prevOffers, ...uniqueNewOffers];
                    });
                }

                //setOffers(response.data);
            } catch (err) {
                setError("Nu s-au putut încărca produsele");
                console.error("Eroare la preluarea ofertelor:", err);
            } finally {
                setLoading(false);
            }
        };
        fetchOffers();
    }, [page,debouncedSearchTerm, category, sortBy]); // Re-rulează când searchTerm se schimbă
    // Funcție pentru adăugarea în Istoric (maximum 6 produse unice)
    const addToRecentlyViewed = (product) => {
        const filtered = recentlyViewed.filter(item => item.id !== product.id);
        const updated = [product, ...filtered].slice(0, 6); // Păstrăm doar ultimele 6
        setRecentlyViewed(updated);
        localStorage.setItem("recentlyViewed", JSON.stringify(updated));
    };

    return (

        <div className="px-4">

            <SearchBar
                searchTerm={searchTerm}
                setSearchTerm={setSearchTerm}
                setPage={setPage}
            />
            {/* Bara de Filtre și Sortare */}
            <FilterBar
                category={category} setCategory={setCategory}
                sortBy={sortBy} setSortBy={setSortBy}
                setPage={setPage}
            />

            {offers.length === 0 && loading && (
                <div className="grid grid-cols-2 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3 sm:gap-6">
                    {Array.from({ length: 8 }).map((_, idx) => (
                        <ProductSkeleton key={idx} />
                    ))}
                </div>
            )}
            {error &&<p className="text-red-500 text-center mt-4">{error}</p>}

            {offers.length === 0 && !loading && !error && (
                <p className="text-center text-gray-500 dark:text-gray-400 mt-4">Nu s-au găsit produse.</p>
            )}

            <div className="grid grid-cols-2 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3 sm:gap-6">
                {offers.map((offer,index) =>{
                    // Dacă este ULTIMUL produs din listă, îi atașăm referința (ref) pentru scroll detector
                    if (offers.length === index + 1) {
                        return (
                            <div ref={lastProductElementRef} key={offer.id}>
                                <ProductCard
                                    offer={offer}
                                    favorites={favorites} setFavorites={setFavorites}
                                    compareItems={compareItems} setCompareItems={setCompareItems}
                                    addToRecentlyViewed={addToRecentlyViewed}
                                />
                            </div>
                        );
                    } else {
                        return(
                            <ProductCard
                                key={offer.id}
                                offer={offer}
                                favorites={favorites}
                                setFavorites={setFavorites}
                                compareItems={compareItems}
                                setCompareItems={setCompareItems}
                                addToRecentlyViewed={addToRecentlyViewed}
                            />
                        );
                    }
                })}
            </div>
            {/* Spinner de încărcare atașat la final */}
            {loading && offers.length > 0 && (
                <div className="grid grid-cols-2 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3 sm:gap-6">
                    {Array.from({ length: 4 }).map((_, idx) => (
                        <ProductSkeleton key={idx} />
                    ))}
                </div>
            )}

            {/* Mesaj când s-a epuizat stocul/baza de date */}
            {!hasMore && offers.length > 0 && (
                <p className="text-center text-gray-400 dark:text-gray-500 my-8 italic font-medium">
                    Ai parcurs toate produsele din această categorie! 🎉
                </p>
            )}
            {/* Secțiunea de Văzute Recent în josul paginii */}
            <RecentlyViewed items={recentlyViewed} />

        </div>
    );
}
export default OfferList;