function SearchBar({ searchTerm, setSearchTerm,setPage  }) {

    return (

        <div className="px-4 py-4">

            <input
                type="text"
                placeholder="Caută produse..."
                value={searchTerm}
                onChange={(e) => {setSearchTerm(e.target.value);setPage(0);}}
                className="
                    w-full
                    p-3
                    border
                    rounded-xl
                    shadow-sm
                    text-base
                "
            />

        </div>
    );
}

export default SearchBar;