function CompareBar({ compareItems=[] }) {

    if(!compareItems.length){
        return null;
    }

    return (

        <div className="
            fixed
            bottom-0
            left-0
            right-0
            bg-white
            shadow-lg
            p-4
            border-t
        ">

            <h3 className="font-bold">
                Produse selectate:
            </h3>

            <div className="
                flex
                gap-4
                overflow-auto
            ">

                {compareItems.map(item=>(

                    <div key={item.id}
                         className="
                            min-w-[150px]
                            p-2
                            border
                            rounded
                        ">

                        {item.name}

                    </div>

                ))}

            </div>

        </div>

    );
}

export default CompareBar;