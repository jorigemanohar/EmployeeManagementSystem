
import { useEffect, useState } from "react";
import { getMyAttendance } from "../services/employeeAttendanceService";

function EmployeeAttendance() {
  const [attendance, setAttendance] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const loadAttendance = async () => {
      try {
        const response = await getMyAttendance();
        setAttendance(response.data);
      } catch (err) {
        console.error("Attendance loading error:", err);
        setError("Unable to load attendance.");
      } finally {
        setLoading(false);
      }
    };

    loadAttendance();
  }, []);

  if (loading) {
    return <p>Loading attendance...</p>;
  }

  if (error) {
    return <p>{error}</p>;
  }

  return (
    <div className="employee-attendance">
      <h2>My Attendance</h2>

      {attendance.length === 0 ? (
        <p>No attendance records found.</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Date</th>
              <th>Status</th>
            </tr>
          </thead>

          <tbody>
            {attendance.map((record) => (
              <tr key={record.id}>
                <td>{record.id}</td>
                <td>{record.attendanceDate}</td>
                <td>{record.status}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default EmployeeAttendance;
