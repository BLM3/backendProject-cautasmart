// import logo from './logo.svg';
import './App.css';
import React, {useState} from 'react';
import Navbar from './components/Navbar';
import OfferList from './components/OfferList';
import CompareBar from './components/CompareBar';
function App() {
    const [compareItems,setCompareItems]=useState([]);
    const [favorites, setFavorites] = useState(() => {
        const saved = localStorage.getItem("favorites");
        return saved ? JSON.parse(saved) : [];
    });

    const [showFavorites, setShowFavorites] = useState(false);

    return (
        <div className="App min-h-screen bg-gray-100">

            <Navbar setShowFavorites={setShowFavorites}/>
            <OfferList
                compareItems={compareItems}
                setCompareItems={setCompareItems}
                favorites={favorites}
                setFavorites={setFavorites}
                showFavorites={showFavorites}
            />
            <CompareBar
                compareItems={compareItems}
            />
        </div>
      );
}

export default App;
