import JobCard from "./JobCard";

function JobCardGrid({ jobs }) {
    return (
        <div className="grid gap-5 sm:grid-cols-2 xl:grid-cols-3">
            {jobs.map((job) => (
                <JobCard key={job.id} job={job} />
            ))}
        </div>
    );
}

export default JobCardGrid;
