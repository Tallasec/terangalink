import HousingDetailGallery from "./HousingDetailGallery";
import HousingDetailInfo from "./HousingDetailInfo";

function HousingDetailContent({ housing }) {
    return (
        <div className="space-y-6">
            <HousingDetailGallery housing={housing} />
            <HousingDetailInfo housing={housing} />
        </div>
    );
}

export default HousingDetailContent;
