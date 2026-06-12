import './App.css';
import { Toaster } from 'react-hot-toast';
import React, {useState} from 'react';
import Navbar from './components/Navbar';
import OfferList from './components/OfferList';
import Favorites from './components/Favorites';
import CompareBar from './components/CompareBar';


function App() {
    const [compareItems,setCompareItems]=useState([]);
    const [favorites, setFavorites] = useState(() => {
        const saved = localStorage.getItem("favorites");
        return saved ? JSON.parse(saved) : [];
    });
    const [showFavorites, setShowFavorites] = useState(false);

    return (
        <div className="min-h-screen bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-gray-100 transition-colors duration-300">
            <Toaster position="top-right" reverseOrder={false} />
            <Navbar setShowFavorites={setShowFavorites} favorites={favorites}/>
                {showFavorites ? (
                    <Favorites
                        favorites={favorites}
                        setFavorites={setFavorites}
                        compareItems={compareItems}
                        setCompareItems={setCompareItems}
                    />
                ) : (
                    <main className="max-w-7xl mx-auto pt-6 pb-12">
                    <OfferList
                        compareItems={compareItems}
                        setCompareItems={setCompareItems}
                        favorites={favorites}
                        setFavorites={setFavorites}
                    />
                    </main>
                )}
                <CompareBar
                    compareItems={compareItems}
                    setCompareItems={setCompareItems}
                />
        </div>
      );
}

export default App;
