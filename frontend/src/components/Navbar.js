import React, {useState,useEffect} from 'react';
import { Heart,Home } from 'lucide-react';
function Navbar({favorites = [],setShowFavorites} ) {
    // Starea pentru Dark Mode (verifică și preferința salvată în localStorage)
    const [darkMode, setDarkMode] = useState(() => {
        return localStorage.getItem("theme") === "dark";
    });
    // Aplică clasa 'dark' pe elementul HTML la fiecare schimbare a stării
    useEffect(() => {
        if (darkMode) {
            document.documentElement.classList.add('dark');
            localStorage.setItem("theme", "dark");
        } else {
            document.documentElement.classList.remove('dark');
            localStorage.setItem("theme", "light");
        }
    }, [darkMode]);
    return (
        <nav className="bg-white dark:bg-gray-900 border-b border-gray-200 dark:border-gray-800 sticky top-0 z-50 transition-colors duration-300">
           <div className="max-w-7xl mx-auto px-4 h-16 flex justify-between items-center">

                <h1 className="text-xl sm:text-2xl font-black text-blue-600 dark:text-blue-400 tracking-tight cursor-pointer" onClick={() => setShowFavorites(false)}>
                AffiliateShop
            </h1>

            <div className="flex items-center gap-3 sm:gap-6">
                <div className="flex items-center space-x-2 sm:space-x-4 font-semibold text-xs sm:text-sm text-gray-600 dark:text-gray-300">
                    <button className="relative flex h-9 px-2 sm:h-11 sm:px-3 items-center justify-center gap-1.5 rounded-xl border border-gray-200 bg-white text-gray-500 transition-all duration-200 hover:scale-105 hover:text-blue-500 hover:border-blue-200 active:scale-95 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300 dark:hover:text-blue-400 dark:hover:border-blue-950/40"
                            onClick={() =>setShowFavorites(false)}title="Acasă">
                        <Home size={16} className="sm:size-5" />
                        <span className="hidden sm:inline">Home</span>
                    </button>

                    <button
                        className="relative flex h-9 px-2 sm:h-11 sm:px-3 items-center justify-center gap-1.5 rounded-xl border border-gray-200 bg-white text-gray-500 transition-all duration-200 hover:scale-105 hover:text-blue-500 hover:border-blue-200 active:scale-95 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300 dark:hover:text-blue-400 dark:hover:border-blue-950/40"
                        onClick={() => setShowFavorites(prev => !prev)}
                        title="Vezi Favorite" >
                        <Heart
                            size={16}
                            className={`sm:size-5 transition-all duration-300 
                                    ${favorites.length > 0 ? 'text-blue-500 fill-current' : ''}`}
                        />
                        <span className="hidden sm:inline">Favorites</span>

                        {/* Badge-ul numeric roșu (apare doar dacă ai produse salvate) */}
                        {favorites.length > 0 && (
                            <span className="absolute -top-1 -right-1 flex h-4 w-4 sm:h-5 sm:w-5 items-center justify-center rounded-full bg-blue-500 text-[9px] sm:text-[10px] font-bold text-white shadow-sm ring-2 ring-white dark:ring-gray-800">
                                    {favorites.length}
                                </span>
                        )}
                    </button>

                    {/*<button className="p-2 hover:text-blue-600 dark:hover:text-blue-400 transition">*/}
                    {/*    🛒 <span className="hidden sm:inline">Cart</span>*/}
                    {/*</button>*/}
                </div>
               <span className="h-5 w-px bg-gray-200 dark:bg-gray-700"></span>
               <button
                   onClick={() => setDarkMode(!darkMode)}
                   className="p-2 rounded-xl bg-gray-100 hover:bg-gray-200 dark:bg-gray-800 dark:hover:bg-gray-700 border border-gray-200 dark:border-gray-700 text-sm transition-all flex items-center justify-center w-9 h-9 sm:w-10 sm:h-10 text-gray-700 dark:text-gray-200"
                   title={darkMode ? "Mod Luminos" : "Mod Întunecat"}
               >{darkMode ? "☀️" : "🌙"}
               </button>
                </div>
           </div>
        </nav>
    );
}

export default Navbar;