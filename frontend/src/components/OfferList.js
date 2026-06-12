import React, { useEffect, useState, useRef, useCallback } from 'react';
import axios from 'axios'; // Instalăm axios pentru cereri HTTP
import { motion } from 'framer-motion';
import { X } from 'lucide-react';
import ProductCard from './ProductCard';
import SearchBar from './SearchBar';
import FilterBar from './FilterBar';
import RecentlyViewed from './RecentlyViewed';
import ProductSkeleton from './ProductSkeleton';

const containerVariants = {
    hidden: { opacity: 0 },
    show: {
        opacity: 1,
        transition: {
            staggerChildren: 0.04 // Fiecare card apare la o diferență de 40ms
        }
    }
};

// Variantele de animație pentru fiecare card individual (Fade-In & Slide-Up)
const itemVariants = {
    hidden: { opacity: 0, y: 15 },
    show: {
        opacity: 1,
        y: 0,
        transition: { type: "spring", stiffness: 110, damping: 16 }
    }
};
function OfferList({favorites, setFavorites, compareItems, setCompareItems}) {
    const [offers, setOffers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [page, setPage] = useState(0);
    const [hasMore, setHasMore] = useState(true); // Verificăm dacă mai sunt produse de adus

    const [searchTerm, setSearchTerm] = useState(() => {
        const params = new URLSearchParams(window.location.search);
        return params.get('search') || '';
    });
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

    useEffect(() => {
        const params = new URLSearchParams(window.location.search);

        // Punem sau eliminăm termul de căutare
        if (searchTerm) params.set('search', searchTerm);
        else params.delete('search');

        // Punem sau eliminăm categoria (dacă e 'All', curățăm URL-ul ca să fie mai elegant)
        if (category && category !== 'All') params.set('category', category);
        else params.delete('category');

        // Punem sau eliminăm sortarea
        if (sortBy) params.set('sortBy', sortBy);
        else params.delete('sortBy');

        // Reconstruim string-ul URL fără a reîncărca pagina
        const newUrl = `${window.location.pathname}${params.toString() ? '?' + params.toString() : ''}`;
        window.history.pushState({ path: newUrl }, '', newUrl);
    }, [searchTerm, category, sortBy]); // Se declanșează instant când se schimbă oricare din cele 3 stări
    // Resetăm lista și pagina la 0 când se schimbă filtrele sau căutarea
    useEffect(() => {
        setOffers([]);
        setPage(0);
        setHasMore(true);
    }, [debouncedSearchTerm, category, sortBy]);

    // Efect pentru DEBOUNCE: Așteaptă 500ms înainte de a schimba debouncedSearchTerm
    useEffect(() => {
        const handler = setTimeout(() => {
            setDebouncedSearchTerm(searchTerm);
        }, 500);

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

            {/* --- ÎMBUNĂTĂȚIREA 3: PASTILE FILTRE ACTIVE --- */}
            {(searchTerm || (category && category !== 'All') || sortBy) && (
                <div className="flex flex-wrap gap-2 my-4 items-center max-w-3xl mx-auto">
                    <span className="text-xs font-semibold text-gray-400 dark:text-gray-500 mr-1">
                        Filtre active:
                    </span>

                    {/* Pastilă Căutare */}
                    {searchTerm && (
                        <div className="flex items-center gap-1.5 bg-blue-50 dark:bg-blue-950/40 text-blue-600 dark:text-blue-400 px-3 py-1 rounded-full text-xs font-medium border border-blue-100 dark:border-blue-900/50 animate-fadeIn">
                            <span>Căutare: "{searchTerm}"</span>
                            <button
                                onClick={() => { setSearchTerm(''); setPage(0); }}
                                className="hover:bg-blue-100 dark:hover:bg-blue-900 p-0.5 rounded-full transition-colors"
                            >
                                <X size={12} />
                            </button>
                        </div>
                    )}

                    {/* Pastilă Categorie */}
                    {category && category !== 'All' && (
                        <div className="flex items-center gap-1.5 bg-purple-50 dark:bg-purple-950/40 text-purple-600 dark:text-purple-400 px-3 py-1 rounded-full text-xs font-medium border border-purple-100 dark:border-purple-900/50 animate-fadeIn">
                            <span>Categorie: {category}</span>
                            <button
                                onClick={() => { setCategory('All'); setPage(0); }}
                                className="hover:bg-purple-100 dark:hover:bg-purple-900 p-0.5 rounded-full transition-colors"
                            >
                                <X size={12} />
                            </button>
                        </div>
                    )}

                    {/* Pastilă Sortare */}
                    {sortBy && (
                        <div className="flex items-center gap-1.5 bg-amber-50 dark:bg-amber-950/40 text-amber-600 dark:text-amber-400 px-3 py-1 rounded-full text-xs font-medium border border-amber-100 dark:border-amber-900/50 animate-fadeIn">
                            <span>
                                Sortare: {sortBy === 'price_asc' ? 'Preț crescător' : sortBy === 'price_desc' ? 'Preț descrescător' : 'Rating'}
                            </span>
                            <button
                                onClick={() => { setSortBy(''); setPage(0); }}
                                className="hover:bg-amber-100 dark:hover:bg-amber-900 p-0.5 rounded-full transition-colors"
                            >
                                <X size={12} />
                            </button>
                        </div>
                    )}

                    {/* Buton Resetare Totală */}
                    <button
                        onClick={() => {
                            setSearchTerm('');
                            setCategory('All');
                            setSortBy('');
                            setPage(0);
                        }}
                        className="text-xs font-bold text-red-500 hover:text-red-600 dark:text-red-400 dark:hover:text-red-300 ml-2 hover:underline transition-colors"
                    >
                        Resetează tot
                    </button>
                </div>
            )}

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

            {/* --- ÎMBUNĂTĂȚIREA 1: GRILA ANIMATĂ CU FRAMER MOTION --- */}
            <motion.div
                variants={containerVariants}
                initial="hidden"
                animate="show"
                // Schimbarea cheii forțează re-executarea animației elegante doar când filtrele principale se modifică
                key={`${debouncedSearchTerm}-${category}-${sortBy}`}
                className="grid grid-cols-2 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3 sm:gap-6"
            >
                {offers.map((offer, index) => {
                    // Dacă este ULTIMUL produs din listă, îi atașăm referința (ref) pentru scroll detector
                    if (offers.length === index + 1) {
                        return (
                            <motion.div variants={itemVariants} ref={lastProductElementRef} key={offer.id}>
                                <ProductCard
                                    offer={offer}
                                    favorites={favorites} setFavorites={setFavorites}
                                    compareItems={compareItems} setCompareItems={setCompareItems}
                                    addToRecentlyViewed={addToRecentlyViewed}
                                />
                            </motion.div>
                        );
                    } else {
                        return(
                            <motion.div variants={itemVariants} key={offer.id}>
                                <ProductCard
                                    offer={offer}
                                    favorites={favorites}
                                    setFavorites={setFavorites}
                                    compareItems={compareItems}
                                    setCompareItems={setCompareItems}
                                    addToRecentlyViewed={addToRecentlyViewed}
                                />
                            </motion.div>
                        );
                    }
                })}
            </motion.div>
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