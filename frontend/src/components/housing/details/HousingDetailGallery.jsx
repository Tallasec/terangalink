import { getPrimaryImageUrl } from "../../../services/housing/housingHelpers";

function HousingDetailGallery({ housing }) {
    const images =
        housing?.images?.length > 0
            ? housing.images
            : [{ id: "placeholder", imageUrl: getPrimaryImageUrl(housing) }];

    return (
        <div className="grid gap-4 md:grid-cols-[1.4fr_1fr]">
            <div className="overflow-hidden rounded-[28px] border border-[#bfc8ca]/40 bg-white shadow-[0px_8px_30px_rgba(0,0,0,0.06)]">
                <img
                    alt={housing.title}
                    className="aspect-[16/10] w-full object-cover"
                    src={images[0].imageUrl}
                />
            </div>

            {images.length > 1 ? (
                <div className="grid grid-cols-2 gap-4">
                    {images.slice(1, 5).map((image) => (
                        <div
                            key={image.id}
                            className="overflow-hidden rounded-2xl border border-[#bfc8ca]/40 bg-white shadow-[0px_4px_20px_rgba(0,0,0,0.04)]"
                        >
                            <img
                                alt={`Photo du logement ${housing.title}`}
                                className="aspect-[4/3] w-full object-cover"
                                src={image.imageUrl}
                            />
                        </div>
                    ))}
                </div>
            ) : null}
        </div>
    );
}

export default HousingDetailGallery;
