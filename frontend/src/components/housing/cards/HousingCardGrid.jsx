import HousingCard from "./HousingCard";

function HousingCardGrid({ housings }) {
    return (
        <div className="grid gap-6 sm:grid-cols-2">
            {housings.map((housing) => (
                <HousingCard key={housing.id} housing={housing} />
            ))}
        </div>
    );
}

export default HousingCardGrid;
