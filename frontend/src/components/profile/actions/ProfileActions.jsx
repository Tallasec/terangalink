import Button from "../../common/ui/Button";

function ProfileActions({ isEditing, loading, onCancel, onEdit }) {
    if (!isEditing) {
        return (
            <div className="mt-8 flex justify-end">
                <Button
                    type="button"
                    className="rounded-xl bg-[#00343a] px-5 py-3 text-[12px] font-semibold uppercase tracking-[0.08em] text-white hover:bg-[#002b30]"
                    onClick={onEdit}
                >
                    Modifier mon profil
                </Button>
            </div>
        );
    }

    return (
        <div className="mt-8 flex flex-col gap-3 sm:flex-row sm:justify-end">
            <Button
                type="button"
                variant="secondary"
                className="rounded-xl px-5 py-3 text-[12px] font-semibold uppercase tracking-[0.08em]"
                disabled={loading}
                onClick={onCancel}
            >
                Annuler
            </Button>
            <Button
                type="submit"
                className="rounded-xl bg-[#00343a] px-5 py-3 text-[12px] font-semibold uppercase tracking-[0.08em] text-white hover:bg-[#002b30]"
                loading={loading}
                disabled={loading}
            >
                Enregistrer
            </Button>
        </div>
    );
}

export default ProfileActions;
