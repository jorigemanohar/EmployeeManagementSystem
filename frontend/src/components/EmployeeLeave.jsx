import { useEffect, useState } from "react";
import {
  getMyLeaves,
  submitLeave,
} from "../services/employeeLeaveService";

function EmployeeLeave() {
  const [leaves, setLeaves] = useState([]);

  const [form, setForm] = useState({
    leaveType: "SICK",
    startDate: "",
    endDate: "",
    reason: "",
  });

  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  /* =========================================================
     LOAD MY LEAVES
  ========================================================= */

  useEffect(() => {
    loadLeaves();
  }, []);

  const loadLeaves = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await getMyLeaves();

      setLeaves(response.data);
    } catch (error) {
      console.error(
        "Error loading employee leaves:",
        error
      );

      if (
        error.response &&
        error.response.status === 403
      ) {
        setError(
          "You are not authorized to view your leave records."
        );
      } else {
        setError(
          "Unable to load leave records."
        );
      }
    } finally {
      setLoading(false);
    }
  };

  /* =========================================================
     FORM CHANGE
  ========================================================= */

  const handleChange = (e) => {
    const { name, value } = e.target;

    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  /* =========================================================
     RESET FORM
  ========================================================= */

  const resetForm = () => {
    setForm({
      leaveType: "SICK",
      startDate: "",
      endDate: "",
      reason: "",
    });

    setError("");
    setSuccess("");
  };

  /* =========================================================
     SUBMIT LEAVE
  ========================================================= */

  const handleSubmit = async (e) => {
    e.preventDefault();

    setError("");
    setSuccess("");

    if (!form.startDate) {
      setError("Please select a start date.");
      return;
    }

    if (!form.endDate) {
      setError("Please select an end date.");
      return;
    }

    if (form.endDate < form.startDate) {
      setError(
        "End date cannot be before the start date."
      );
      return;
    }

    if (!form.reason.trim()) {
      setError(
        "Please enter a reason for leave."
      );
      return;
    }

    setSubmitting(true);

    try {
      /*
       * Employee ID is NOT sent from the frontend.
       * The backend identifies the logged-in employee.
       *
       * Leave status is also NOT sent.
       * The backend automatically sets it to PENDING.
       */

      const leaveData = {
        leaveType: form.leaveType,
        startDate: form.startDate,
        endDate: form.endDate,
        reason: form.reason.trim(),
      };

      await submitLeave(leaveData);

      setSuccess(
        "Leave request submitted successfully. Status: PENDING."
      );

      resetForm();

      /*
       * Reload records so the newly submitted
       * request appears immediately.
       */
      await loadLeaves();
    } catch (error) {
      console.error(
        "Error submitting leave:",
        error
      );

      if (
        error.response &&
        error.response.status === 403
      ) {
        setError(
          "You are not authorized to submit a leave request."
        );
      } else if (
        error.response &&
        typeof error.response.data ===
          "string"
      ) {
        setError(
          error.response.data
        );
      } else {
        setError(
          "Unable to submit leave request."
        );
      }
    } finally {
      setSubmitting(false);
    }
  };

  /* =========================================================
     STATUS CLASS
  ========================================================= */

  const getStatusClass = (status) => {
    switch (status) {
      case "APPROVED":
        return "approved";

      case "REJECTED":
        return "rejected";

      case "PENDING":
      default:
        return "pending";
    }
  };

  /* =========================================================
     STATUS ICON
  ========================================================= */

  const getStatusIcon = (status) => {
    switch (status) {
      case "APPROVED":
        return "✅";

      case "REJECTED":
        return "❌";

      case "PENDING":
      default:
        return "🟡";
    }
  };

  /* =========================================================
     LOADING
  ========================================================= */

  if (loading) {
    return (
      <section className="employee-section">
        <h2>My Leave</h2>
        <p>Loading leave records...</p>
      </section>
    );
  }

  /* =========================================================
     PAGE
  ========================================================= */

  return (
    <section className="employee-section">

      <div className="employee-section-header">
        <div>
          <h2>My Leave</h2>

          <p>
            Submit leave requests and track
            their approval status.
          </p>
        </div>
      </div>

      {/* =====================================================
          ERROR MESSAGE
      ===================================================== */}

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      {/* =====================================================
          SUCCESS MESSAGE
      ===================================================== */}

      {success && (
        <div className="success-message">
          {success}
        </div>
      )}

      {/* =====================================================
          SUBMIT LEAVE
      ===================================================== */}

      <section className="form-section">

        <h3>
          Submit Leave Request
        </h3>

        <form
          onSubmit={handleSubmit}
          className="data-form"
        >

          <div className="form-grid">

            {/* Leave Type */}

            <div className="form-group">

              <label htmlFor="leaveType">
                Leave Type
              </label>

              <select
                id="leaveType"
                name="leaveType"
                value={form.leaveType}
                onChange={handleChange}
                disabled={submitting}
                required
              >
                <option value="SICK">
                  Sick Leave
                </option>

                <option value="CASUAL">
                  Casual Leave
                </option>

                <option value="EARNED">
                  Earned Leave
                </option>
              </select>

            </div>

            {/* Start Date */}

            <div className="form-group">

              <label htmlFor="startDate">
                Start Date
              </label>

              <input
                id="startDate"
                type="date"
                name="startDate"
                value={form.startDate}
                onChange={handleChange}
                disabled={submitting}
                required
              />

            </div>

            {/* End Date */}

            <div className="form-group">

              <label htmlFor="endDate">
                End Date
              </label>

              <input
                id="endDate"
                type="date"
                name="endDate"
                value={form.endDate}
                onChange={handleChange}
                disabled={submitting}
                required
              />

            </div>

            {/* Reason */}

            <div className="form-group full-width">

              <label htmlFor="reason">
                Reason
              </label>

              <textarea
                id="reason"
                name="reason"
                value={form.reason}
                onChange={handleChange}
                placeholder="Enter reason for leave"
                rows="4"
                disabled={submitting}
                required
              />

            </div>

          </div>

          {/* Buttons */}

          <div className="form-actions">

            <button
              type="submit"
              disabled={submitting}
            >
              {submitting
                ? "Submitting..."
                : "Submit Leave Request"}
            </button>

            <button
              type="button"
              onClick={resetForm}
              disabled={submitting}
            >
              Clear
            </button>

          </div>

          <p className="form-note">
            New leave requests are automatically
            submitted with{" "}
            <strong>PENDING</strong> status and
            reviewed by the administrator.
          </p>

        </form>

      </section>

      {/* =====================================================
          LEAVE RECORDS
      ===================================================== */}

      <section className="data-section">

        <div className="section-header">

          <div>
            <h3>
              My Leave Records
            </h3>

            <p>
              View the current status of all
              your leave requests.
            </p>
          </div>

          <button
            type="button"
            onClick={loadLeaves}
            disabled={loading}
          >
            Refresh
          </button>

        </div>

        {leaves.length === 0 ? (

          <div className="empty-state">

            <p>
              No leave requests found.
            </p>

          </div>

        ) : (

          <div className="table-container">

            <table>

              <thead>

                <tr>
                  <th>ID</th>
                  <th>Leave Type</th>
                  <th>Start Date</th>
                  <th>End Date</th>
                  <th>Reason</th>
                  <th>Status</th>
                </tr>

              </thead>

              <tbody>

                {leaves.map(
                  (leave) => (

                    <tr
                      key={leave.id}
                    >

                      <td>
                        {leave.id}
                      </td>

                      <td>
                        {leave.leaveType}
                      </td>

                      <td>
                        {leave.startDate}
                      </td>

                      <td>
                        {leave.endDate}
                      </td>

                      <td>
                        {leave.reason ||
                          "-"}
                      </td>

                      <td>

                        <span
                          className={`leave-status ${getStatusClass(
                            leave.status
                          )}`}
                        >
                          {getStatusIcon(
                            leave.status
                          )}{" "}
                          {leave.status}
                        </span>

                      </td>

                    </tr>

                  )
                )}

              </tbody>

            </table>

          </div>

        )}

      </section>

    </section>
  );
}

export default EmployeeLeave;