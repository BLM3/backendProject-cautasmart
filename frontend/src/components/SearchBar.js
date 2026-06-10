import React, { useState } from 'react';
import toast from 'react-hot-toast';
import { Mic, MicOff } from 'lucide-react';

function SearchBar({ searchTerm, setSearchTerm, setPage }) {
    const [isListening, setIsListening] = useState(false);

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
        <div className="max-w-3xl mx-auto my-6 px-4 relative flex items-center">
            <input
                type="text"
                placeholder={isListening ? "Te ascult..." : "Caută produse..."}
                value={searchTerm}
                onChange={(e) => {
                    setSearchTerm(e.target.value);
                    setPage(0);
                }}
                className={`w-full p-4 pr-12 rounded-xl border bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 placeholder-gray-400 focus:outline-none focus:ring-2 shadow-sm transition-all
                    ${isListening
                    ? 'border-red-400 focus:ring-red-400 animate-pulse bg-red-50/10 dark:bg-red-950/10'
                    : 'border-gray-200 dark:border-gray-700 focus:ring-blue-500'
                }`}
                disabled={isListening}
            />

            <button
                type="button"
                onClick={handleVoiceSearch}
                className={`absolute right-8 p-2 rounded-xl transition-all duration-200 active:scale-95
                    ${isListening
                    ? 'text-red-500 scale-110 bg-red-50 dark:bg-red-950/40'
                    : 'text-gray-400 hover:text-blue-500 dark:text-gray-500 dark:hover:text-blue-400 hover:bg-gray-50 dark:hover:bg-gray-700/50'
                }`}
                title="Căutare vocală"
            >
                {/* Acum iconițele sunt folosite corect! */}
                {isListening ? (
                    <MicOff size={22} className="animate-bounce" />
                ) : (
                    <Mic size={22} />
                )}
            </button>
        </div>
    );
}

export default SearchBar;