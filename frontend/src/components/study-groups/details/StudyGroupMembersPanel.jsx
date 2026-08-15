import MaterialSymbol from "../../common/MaterialSymbol";

function StudyGroupMembersPanel({ members }) {
    return (
        <div className="rounded-[28px] border border-[#dbe6e6] bg-white p-6 shadow-[0px_12px_40px_rgba(0,52,58,0.06)]">
            <div className="flex items-start justify-between gap-4">
                <div>
                    <p className="text-[12px] font-semibold uppercase tracking-[0.16em] text-[#70797a]">
                        Membres
                    </p>
                    <h2 className="mt-2 text-[22px] font-semibold text-[#00343a]">
                        Personnes dans ce groupe
                    </h2>
                </div>
                <span className="rounded-full bg-[#eef8f8] px-3 py-1 text-[12px] font-semibold text-[#00343a]">
                    {members.length}
                </span>
            </div>

            <div className="mt-5 space-y-3">
                {members.map((member) => (
                    <div
                        key={member.id}
                        className="flex items-start justify-between gap-4 rounded-[22px] border border-[#edf3f3] bg-[#f7fafb] p-4"
                    >
                        <div>
                            <p className="text-[15px] font-semibold text-[#181c1d]">
                                {member.firstName} {member.lastName}
                            </p>
                            <p className="mt-1 text-[13px] text-[#526062]">
                                {member.fieldOfStudy} • {member.university}
                            </p>
                            <p className="mt-1 text-[13px] text-[#70797a]">{member.city}</p>
                        </div>
                        <MaterialSymbol icon="person" className="text-[#00343a]" />
                    </div>
                ))}
            </div>
        </div>
    );
}

export default StudyGroupMembersPanel;
