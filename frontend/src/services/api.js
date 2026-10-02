import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api",

  headers: {
    "Content-Type": "application/json",
  },
});

// =========================================================
// REQUEST INTERCEPTOR
// =========================================================

api.interceptors.request.use(
  (config) => {

    const token =
      localStorage.getItem("token");

    if (token) {
      config.headers.Authorization =
        `Bearer ${token}`;
    }

    return config;
  },

  (error) => {
    return Promise.reject(error);
  }
);

// =========================================================
// RESPONSE INTERCEPTOR
// =========================================================

api.interceptors.response.use(
  (response) => {
    return response;
  },

  (error) => {

    if (
      error.response &&
      error.response.status === 401
    ) {

      console.warn(
        "Authentication expired or invalid."
      );

      localStorage.removeItem(
        "token"
      );

      /*
       * Reload the application.
       * App.js will detect that no token
       * exists and display the Login page.
       */
      window.location.reload();
    }

    return Promise.reject(error);
  }
);

export default api;