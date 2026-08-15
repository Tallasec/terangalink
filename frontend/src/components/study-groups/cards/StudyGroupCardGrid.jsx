import StudyGroupCard from "./StudyGroupCard";

function StudyGroupCardGrid({ groups }) {
    return (
        <div className="grid gap-6 md:grid-cols-2 xl:grid-cols-3">
            {groups.map((group) => (
                <StudyGroupCard key={group.id} group={group} />
            ))}
        </div>
    );
}

export default StudyGroupCardGrid;
