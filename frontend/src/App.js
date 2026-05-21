// import logo from './logo.svg';
import './App.css';
import React from 'react';
import Navbar from './components/Navbar';
import OfferList from './components/OfferList';
import CompareBar from './components/CompareBar';
function App() {
  return (
    <div className="App min-h-screen bg-gray-100">

        <Navbar />
        <OfferList />
        <CompareBar />
    </div>
  );
}

export default App;
