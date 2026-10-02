function AttendanceSummary({ attendance }) {
    const presentCount = attendance.filter(
        (record) => record.status === "PRESENT"
    ).length;

    const absentCount = attendance.filter(
        (record) => record.status === "ABSENT"
    ).length;

    const leaveCount = attendance.filter(
        (record) => record.status === "LEAVE"
    ).length;

    const totalAttendance =
        presentCount +
        absentCount +
        leaveCount;

    const attendancePercentage =
        totalAttendance === 0
            ? 0
            : (
                  (presentCount / totalAttendance) *
                  100
              ).toFixed(2);

    return (
        <div className="attendance-summary">

            <h3>
                Attendance Statistics
            </h3>

            <div className="attendance-stats">

                <div className="attendance-stat">
                    <span>
                        Present
                    </span>

                    <strong>
                        {presentCount}
                    </strong>
                </div>

                <div className="attendance-stat">
                    <span>
                        Absent
                    </span>

                    <strong>
                        {absentCount}
                    </strong>
                </div>

                <div className="attendance-stat">
                    <span>
                        Leave
                    </span>

                    <strong>
                        {leaveCount}
                    </strong>
                </div>

                <div className="attendance-stat attendance-percentage">
                    <span>
                        Attendance Percentage
                    </span>

                    <strong>
                        {attendancePercentage}%
                    </strong>
                </div>

            </div>

        </div>
    );
}

export default AttendanceSummary;