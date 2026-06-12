import React, { useState, useEffect, useRef } from 'react';
import toast from 'react-hot-toast';
import { Mic, MicOff, X, Clock} from 'lucide-react';

function SearchBar({ searchTerm, setSearchTerm, setPage }) {
    const [isListening, setIsListening] = useState(false);
    const [showHistory, setShowHistory] = useState(false);
    const dropdownRef = useRef(null);

    // Starea pentru istoricul căutărilor citită din localStorage
    const [searchHistory, setSearchHistory] = useState(() => {
        const saved = localStorage.getItem("searchHistory");
        return saved ? JSON.parse(saved) : [];
    });
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                setShowHistory(false);
            }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);
    // Funcție pentru salvarea unui cuvânt nou în istoric (fără duplicate, max 5 căutări)
    const saveToHistory = (word) => {
        if (!word || word.trim() === "") return;
        const cleanWord = word.trim();
        const filtered = searchHistory.filter(item => item.toLowerCase() !== cleanWord.toLowerCase());
        const updated = [cleanWord, ...filtered].slice(0, 5); // Păstrăm ultimele 5
        setSearchHistory(updated);
        localStorage.setItem("searchHistory", JSON.stringify(updated));
    };

    // Ștergerea unui singur element din istoric (când dă click pe X-ul din dropdown)
    const deleteHistoryItem = (e, itemToDelete) => {
        e.stopPropagation(); // Prevenim activarea căutării când dăm click pe ștergere
        const updated = searchHistory.filter(item => item !== itemToDelete);
        setSearchHistory(updated);
        localStorage.setItem("searchHistory", JSON.stringify(updated));
    };

    // Ascultăm când utilizatorul apasă ENTER în input pentru a salva cuvântul
    const handleKeyDown = (e) => {
        if (e.key === 'Enter') {
            saveToHistory(searchTerm);
            setShowHistory(false);
            e.target.blur(); // Scoate focusul din input
        }
    };
    const handleVoiceSearch = () => {
        const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;

        if (!SpeechRecognition) {
            toast.error("Dispozitivul tău nu suportă căutarea vocală. Folosește Chrome (Android) sau Safari (iOS)!");
            return;
        }

        const recognition = new SpeechRecognition();
        recognition.lang = 'ro-RO';
        recognition.interimResults = false;
        recognition.maxAlternatives = 1;

        const isMobile = /iPhone|iPad|iPod|Android/i.test(navigator.userAgent);
        recognition.continuous = !isMobile;

        recognition.onstart = () => {
            setIsListening(true);
            toast('Te ascult... Vorbește acum 🎙️', { id: 'listening-toast', duration: 5000 });
        };

        recognition.onerror = (event) => {
            console.error("Eroare recunoaștere vocală:", event.error);
            setIsListening(false);
            toast.dismiss('listening-toast');

            if (event.error === 'not-allowed') {
                toast.error("Accesul la microfon a fost blocat. Activează permisiunea din browser!");
            } else if (event.error === 'no-speech') {
                toast.error("Nu s-a auzit niciun sunet. Încearcă din nou!");
            } else {
                toast.error("Eroare vocală. Reîncearcă!");
            }
        };

        recognition.onend = () => {
            setIsListening(false);
            toast.dismiss('listening-toast');
        };

        recognition.onresult = (event) => {
            if (event.results && event.results[0]) {
                const speechToText = event.results[0][0].transcript;
                const queryCurat = speechToText.replace(/\.$/, '');
                setSearchTerm(queryCurat);
                setPage(0);
                toast.success(`Căutare pentru: "${queryCurat}"`);
            }
        };

        try {
            recognition.start();
        } catch (e) {
            console.error(e);
        }
    };

    return (
        <div ref={dropdownRef} className="max-w-3xl mx-auto my-6 px-4 relative">
            <div className="relative flex items-center">
                <input
                    type="text"
                    placeholder={isListening ? "Te ascult..." : "Caută produse..."}
                    value={searchTerm}
                    onKeyDown={handleKeyDown}
                    onFocus={() => { if (searchTerm === '') setShowHistory(true); }}
                    onChange={(e) => {
                        setSearchTerm(e.target.value);
                        setPage(0);
                        if (e.target.value === '') setShowHistory(true);
                        else setShowHistory(false);
                    }}
                    className={`w-full p-4 pr-24 rounded-xl border bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 placeholder-gray-400 focus:outline-none focus:ring-2 shadow-sm transition-all
                        ${isListening
                        ? 'border-red-400 focus:ring-red-400 animate-pulse bg-red-50/10'
                        : 'border-gray-200 dark:border-gray-700 focus:ring-blue-500'
                    }`}
                    disabled={isListening}
                />

                {/* --- BUTONUL DE ȘTERGERE TEXT (X) --- */}
                {searchTerm && !isListening && (
                    <button
                        type="button"
                        onClick={() => {
                            setSearchTerm('');
                            setPage(0);
                            setShowHistory(true); // Redeschidem istoricul când se golește barul
                        }}
                        className="absolute right-14 p-1.5 rounded-lg text-gray-400 hover:text-gray-600 dark:text-gray-500 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 transition-all"
                        title="Șterge textul"
                    >
                        <X size={18} />
                    </button>
                )}

                {/* Butonul de Microfon */}
                <button
                    type="button"
                    onClick={handleVoiceSearch}
                    className={`absolute right-4 p-2 rounded-xl transition-all
                        ${isListening
                        ? 'text-red-500 scale-110 bg-red-50 dark:bg-red-950/40'
                        : 'text-gray-400 hover:text-blue-500 dark:text-gray-500 dark:hover:text-blue-400'
                    }`}
                >
                    {isListening ? <MicOff size={20} className="animate-bounce" /> : <Mic size={20} />}
                </button>
            </div>

            {/* --- DROPDOWN PENTRU CĂUTĂRI RECENTE --- */}
            {showHistory && searchHistory.length > 0 && (
                <div className="absolute left-4 right-4 mt-1 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-xl shadow-xl z-50 overflow-hidden transition-all duration-200 animate-fadeIn">
                    <div className="p-2.5 text-xs font-semibold text-gray-400 dark:text-gray-500 border-b border-gray-100 dark:border-gray-700 bg-gray-50/50 dark:bg-gray-800/50">
                        Căutări recente
                    </div>
                    <ul>
                        {searchHistory.map((item, index) => (
                            <li
                                key={index}
                                onClick={() => {
                                    setSearchTerm(item);
                                    setPage(0);
                                    setShowHistory(false);
                                }}
                                className="flex items-center justify-between px-4 py-3 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700/60 cursor-pointer transition-colors group"
                            >
                                <div className="flex items-center gap-2.5">
                                    <Clock size={14} className="text-gray-400 dark:text-gray-500 group-hover:text-blue-500" />
                                    <span>{item}</span>
                                </div>
                                {/* X-ul mic din dreptul fiecărui cuvânt pentru ștergerea lui din istoric */}
                                <button
                                    type="button"
                                    onClick={(e) => deleteHistoryItem(e, item)}
                                    className="p-1 rounded-md text-gray-400 hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-950/40 opacity-0 group-hover:opacity-100 transition-all duration-150"
                                    title="Șterge din istoric"
                                >
                                    <X size={14} />
                                </button>
                            </li>
                        ))}
                    </ul>
                </div>
            )}
        </div>
    );
}

export default SearchBar;