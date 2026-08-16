import AssociationCard from "./AssociationCard";

function AssociationCardGrid({ associations }) {
    return (
        <div className="grid grid-cols-1 gap-6 md:grid-cols-2 xl:grid-cols-3">
            {associations.map((association) => (
                <AssociationCard key={association.id} association={association} />
            ))}
        </div>
    );
}

export default AssociationCardGrid;
