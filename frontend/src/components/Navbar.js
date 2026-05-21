
function Navbar() {
    return (
        <nav className="bg-black text-white px-6 py-4 flex justify-between items-center">

            <h1 className="text-2xl font-bold text-blue-400">
                AffiliateShop
            </h1>

            <div className="space-x-4">
                <button className="hover:text-blue-400">
                    Home
                </button>

                <button className="hover:text-blue-400">
                    Favorites
                </button>

                <button className="hover:text-blue-400">
                    Cart
                </button>
            </div>

        </nav>
    );
}

export default Navbar;