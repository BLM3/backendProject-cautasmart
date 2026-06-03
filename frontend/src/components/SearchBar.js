import React, { useState } from 'react';
import toast from 'react-hot-toast';
function SearchBar({ searchTerm, setSearchTerm,setPage  }) {
    const [isListening, setIsListening] = useState(false);

    const handleVoiceSearch = () => {
        // Verificăm dacă browserul suportă recunoașterea vocală
        const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;

        if (!SpeechRecognition) {
            toast.error("Browserul tău nu suportă căutarea vocală. Încearcă pe Chrome sau Edge!");
            return;
        }

        const recognition = new SpeechRecognition();
        recognition.lang = 'ro-RO'; // Setăm limba română
        recognition.interimResults = false;
        recognition.maxAlternatives = 1;

        recognition.onstart = () => {
            setIsListening(true);
            toast('Te ascult... Vorbește acum 🎙️', { id: 'listening-toast' });
        };

        recognition.onerror = (event) => {
            console.error("Eroare recunoaștere vocală:", event.error);
            setIsListening(false);
            toast.dismiss('listening-toast');
            toast.error("Nu am putut înțelege. Încearcă din nou!");
        };

        recognition.onend = () => {
            setIsListening(false);
            toast.dismiss('listening-toast');
        };

        recognition.onresult = (event) => {
            const speechToText = event.results[0][0].transcript;
            setSearchTerm(speechToText); // Punem textul în search bar
            setPage(0); // Resetăm pagina la 0
            toast.success(`Căutare pentru: "${speechToText}"`);
        };

        recognition.start();
    };
    return (

        <div className="max-w-3xl mx-auto my-6 px-4 relative flex items-center">

            <input
                type="text"
                placeholder="Caută produse..."
                value={searchTerm}
                onChange={(e) => {setSearchTerm(e.target.value);setPage(0);}}
                className="w-full p-4 rounded-xl border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 shadow-sm transition-colors"
            />
            {/* Butonul de Microfon */}
            <button
                type="button"
                onClick={handleVoiceSearch}
                className={`absolute right-7 p-2 rounded-lg transition-all ${isListening ? 'text-red-500 animate-pulse scale-110' : 'text-gray-400 hover:text-blue-500 dark:text-gray-500 dark:hover:text-blue-400'}`}
                title="Căutare vocală"
            >
                {isListening ? (
                    <span className="text-xl">🛑</span>
                ) : (
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 11a7 7 0 01-7 7m0 0a7 7 0 01-7-7m7 7v4m0 0H8m4 0h4m-4-8a3 3 0 01-3-3V5a3 3 0 116 0v6a3 3 0 01-3 3z" />
                    </svg>
                )}
            </button>

        </div>
    );
}

export default SearchBar;