import api from "../api";
import { AUTH_ERROR_CODES } from "../../types/auth";
import { clearAuthStorage } from "./authStorage";

function getFirstDetail(details) {
  if (!details || typeof details !== "object") {
    return "";
  }

  for (const value of Object.values(details)) {
    if (typeof value === "string" && value.trim() !== "") {
      return value;
    }
  }

  return "";
}

export function getAuthErrorMessage(error, fallback = "Une erreur est survenue.") {
  const responseData = error?.response?.data;

  if (!responseData) {
    return fallback;
  }

  const errorCode = responseData.error || responseData.code || responseData.errorCode;

  if (errorCode === AUTH_ERROR_CODES.INVALID_EMAIL_VERIFICATION_TOKEN) {
    return "Le code est invalide.";
  }

  if (errorCode === AUTH_ERROR_CODES.EXPIRED_EMAIL_VERIFICATION_TOKEN) {
    return "Votre code a expiré.";
  }

  return responseData.message || getFirstDetail(responseData.details) || fallback;
}

export async function registerUser(payload) {
  const response = await api.post("/auth/register", payload, {
    skipAuth: true,
  });
  return response.data;
}

export async function loginUser(payload) {
  const response = await api.post("/auth/login", payload, {
    skipAuth: true,
  });
  return response.data;
}

export async function verifyEmail(token) {
  await api.post(
    "/auth/verify-email",
    {
      token,
    },
    {
      skipAuth: true,
    },
  );

  return {
    message: "Votre adresse e-mail a été vérifiée.",
  };
}

export async function resendVerificationEmail(email) {
  const response = await api.post(
    "/auth/resend-verification-email",
    {
      email,
    },
    {
      skipAuth: true,
    },
  );

  return response.data;
}

export function logoutUser() {
  clearAuthStorage();
}
