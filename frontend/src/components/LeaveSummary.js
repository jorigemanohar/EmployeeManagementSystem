function LeaveSummary({ leaves }) {
    const pendingCount = leaves.filter(
        (leave) => leave.status === "PENDING"
    ).length;

    const approvedCount = leaves.filter(
        (leave) => leave.status === "APPROVED"
    ).length;

    const rejectedCount = leaves.filter(
        (leave) => leave.status === "REJECTED"
    ).length;

    const totalCount = leaves.length;

    return (
        <div className="leave-summary">

            <h3>
                Leave Statistics
            </h3>

            <div className="leave-stats">

                <div className="leave-stat">
                    <span>
                        Pending
                    </span>

                    <strong>
                        {pendingCount}
                    </strong>
                </div>

                <div className="leave-stat">
                    <span>
                        Approved
                    </span>

                    <strong>
                        {approvedCount}
                    </strong>
                </div>

                <div className="leave-stat">
                    <span>
                        Rejected
                    </span>

                    <strong>
                        {rejectedCount}
                    </strong>
                </div>

                <div className="leave-stat">
                    <span>
                        Total
                    </span>

                    <strong>
                        {totalCount}
                    </strong>
                </div>

            </div>

        </div>
    );
}

export default LeaveSummary;