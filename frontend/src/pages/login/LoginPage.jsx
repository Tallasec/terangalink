import { useState } from "react";
import { Link, useLocation, useNavigate, useSearchParams } from "react-router-dom";

import { getAuthErrorMessage, loginUser, resendVerificationEmail } from "../../services/auth/authService";
import { setAccessToken } from "../../services/auth/authStorage";
import { AUTH_ERROR_CODES, PENDING_VERIFICATION_EMAIL_KEY } from "../../types/auth";

import PageLayout from "../../components/common/layout/PageLayout";
import Container from "../../components/common/layout/Container";
import Card from "../../components/common/ui/Card";
import Logo from "../../components/common/Logo";
import Input from "../../components/common/ui/Input";
import Button from "../../components/common/ui/Button";

const SESSION_EXPIRED_MESSAGE = "Votre session a expiré. Veuillez vous reconnecter.";

function LoginPage() {
    const location = useLocation();
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();

    const initialEmail = location.state?.email || sessionStorage.getItem(PENDING_VERIFICATION_EMAIL_KEY) || "";

    const [email, setEmail] = useState(initialEmail);
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState(
        location.state?.message || (searchParams.get("session") === "expired" ? SESSION_EXPIRED_MESSAGE : ""),
    );
    const [error, setError] = useState("");
    const [errorCode, setErrorCode] = useState("");
    const [resendMessage, setResendMessage] = useState("");
    const [loading, setLoading] = useState(false);
    const [loadingResend, setLoadingResend] = useState(false);

    async function handleSubmit(event) {
        event.preventDefault();
        setError("");
        setErrorCode("");
        setResendMessage("");
        setMessage("");

        try {
            setLoading(true);

            const response = await loginUser({
                email,
                password,
            });

            if (response?.accessToken) {
                setAccessToken(response.accessToken);
            }

            navigate("/dashboard", { replace: true });
        } catch (requestError) {
            const responseData = requestError?.response?.data;
            const code = responseData?.error || responseData?.code || responseData?.errorCode || "";

            setErrorCode(code);
            setError(getAuthErrorMessage(requestError, "Le serveur est inaccessible."));
        } finally {
            setLoading(false);
        }
    }

    async function handleResendVerification() {
        const trimmedEmail = email.trim();

        setError("");
        setErrorCode("");

        if (!trimmedEmail) {
            setError("Saisissez votre adresse e-mail pour renvoyer le code.");
            return;
        }

        try {
            setLoadingResend(true);
            const response = await resendVerificationEmail(trimmedEmail);
            const successMessage = response?.message || "Un nouveau code a été envoyé.";

            sessionStorage.setItem(PENDING_VERIFICATION_EMAIL_KEY, trimmedEmail);
            setResendMessage(successMessage);
            setMessage("");
        } catch (requestError) {
            setError(getAuthErrorMessage(requestError, "Le serveur est inaccessible."));
        } finally {
            setLoadingResend(false);
        }
    }

    const shouldShowResendAction = errorCode === AUTH_ERROR_CODES.EMAIL_NOT_VERIFIED && Boolean(email.trim());

    return (
        <PageLayout>
            <Container>
                <Card className="mx-auto w-full max-w-md">
                    <div className="mb-8 text-center">
                        <Logo className="text-2xl" />
                    </div>

                    <div className="mb-8 text-center">
                        <h1 className="text-3xl font-bold text-slate-900">Connexion</h1>
                        <p className="mt-2 text-sm text-slate-600">
                            Connectez-vous à votre compte TerangaLink
                        </p>
                    </div>

                    {message && (
                        <div className="mb-6 rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-900">
                            {message}
                        </div>
                    )}

                    {error && (
                        <div className="mb-6 rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-900">
                            <div className="flex items-start gap-3">
                                <span className="material-symbols-outlined mt-0.5 text-[18px]">error</span>
                                <div className="min-w-0 flex-1">
                                    <p>{error}</p>
                                    {shouldShowResendAction && (
                                        <button
                                            type="button"
                                            onClick={handleResendVerification}
                                            disabled={loadingResend}
                                            className="mt-3 inline-flex items-center rounded-lg border border-rose-300 px-3 py-2 text-xs font-semibold text-rose-900 transition-colors hover:bg-rose-100 disabled:cursor-not-allowed disabled:opacity-50"
                                        >
                                            {loadingResend ? "Renvoi en cours..." : "Renvoyer le code"}
                                        </button>
                                    )}
                                </div>
                            </div>
                        </div>
                    )}

                    {resendMessage && (
                        <div className="mb-6 rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-900">
                            <div className="flex items-start gap-3">
                                <span className="material-symbols-outlined mt-0.5 text-[18px]">check_circle</span>
                                <div className="min-w-0 flex-1">
                                    <p>{resendMessage}</p>
                                    <Link
                                        to="/verify-email"
                                        state={{ email: email.trim(), message: resendMessage }}
                                        className="mt-3 inline-flex items-center rounded-lg bg-[#00343a] px-3 py-2 text-xs font-semibold text-white transition-colors hover:bg-[#002b30]"
                                    >
                                        Aller à la vérification
                                    </Link>
                                </div>
                            </div>
                        </div>
                    )}

                    <form className="space-y-5" onSubmit={handleSubmit}>
                        <Input
                            label="Email"
                            type="email"
                            placeholder="Votre adresse email"
                            name="email"
                            required
                            value={email}
                            onChange={(event) => {
                                setEmail(event.target.value);
                            }}
                            disabled={loading || loadingResend}
                        />

                        <Input
                            label="Mot de passe"
                            type="password"
                            placeholder="Votre mot de passe"
                            name="password"
                            required
                            value={password}
                            onChange={(event) => {
                                setPassword(event.target.value);
                            }}
                            disabled={loading || loadingResend}
                        />

                        <Button type="submit" className="w-full" loading={loading} disabled={loading || loadingResend}>
                            Se connecter
                        </Button>
                    </form>

                    <div className="mt-6 space-y-4 text-center">
                        <a
                            href="#"
                            className="text-sm font-medium text-green-600 hover:text-green-700"
                        >
                            Mot de passe oublié ?
                        </a>

                        <p className="text-sm text-slate-600">
                            Vous n&apos;avez pas de compte ?{" "}
                            <Link
                                to="/register"
                                className="font-medium text-green-600 hover:text-green-700"
                            >
                                Créer un compte
                            </Link>
                        </p>
                    </div>
                </Card>
            </Container>
        </PageLayout>
    );
}

export default LoginPage;
