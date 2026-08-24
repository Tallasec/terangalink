import { useCallback, useEffect, useRef, useState } from "react";
import { Link, useLocation, useNavigate, useSearchParams } from "react-router-dom";
import { AlertCircle, ArrowRight, CheckCircle2, Mail, RefreshCw, ShieldCheck } from "lucide-react";

import PageLayout from "../../components/common/layout/PageLayout";
import Container from "../../components/common/layout/Container";
import Card from "../../components/common/ui/Card";
import Button from "../../components/common/ui/Button";
import Input from "../../components/common/ui/Input";
import { getAuthErrorMessage, resendVerificationEmail, verifyEmail } from "../../services/auth/authService";
import { AUTH_ERROR_CODES, PENDING_VERIFICATION_EMAIL_KEY } from "../../types/auth";

function VerifyEmailPage() {
    const location = useLocation();
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const autoVerifyTriggeredRef = useRef(false);

    const isProfileFlow = location.pathname === "/profile/verify-email";
    const tokenFromQuery = searchParams.get("token")?.trim() || "";
    const returnTo = location.state?.returnTo || (isProfileFlow ? "/profile" : "/login");

    const [email] = useState(() => {
        return location.state?.email || sessionStorage.getItem(PENDING_VERIFICATION_EMAIL_KEY) || "";
    });
    const [token, setToken] = useState(tokenFromQuery);
    const [message, setMessage] = useState(
        location.state?.message ||
            (isProfileFlow
                ? "Saisissez le code envoyé à votre nouvelle adresse e-mail."
                : ""),
    );
    const [error, setError] = useState("");
    const [loadingVerify, setLoadingVerify] = useState(false);
    const [loadingResend, setLoadingResend] = useState(false);
    const [showResendAction, setShowResendAction] = useState(false);

    const canResend = Boolean(email) && !loadingVerify && !loadingResend;
    const hasVerificationContext = Boolean(tokenFromQuery || email || location.state?.message);

    useEffect(() => {
        if (email) {
            sessionStorage.setItem(PENDING_VERIFICATION_EMAIL_KEY, email);
        }
    }, [email]);

    useEffect(() => {
        if (isProfileFlow) {
            return;
        }

        if (!hasVerificationContext) {
            navigate("/login", {
                replace: true,
                state: {
                    message: "Veuillez vous connecter pour continuer.",
                },
            });
        }
    }, [hasVerificationContext, isProfileFlow, navigate]);

    const submitVerification = useCallback(async (nextToken) => {
        const trimmedToken = nextToken.trim();

        setError("");
        setMessage("");
        setShowResendAction(false);

        if (trimmedToken.length !== 6) {
            setError("Le code doit contenir 6 chiffres.");
            return;
        }

        try {
            setLoadingVerify(true);

            await verifyEmail(trimmedToken);
            sessionStorage.removeItem(PENDING_VERIFICATION_EMAIL_KEY);

            navigate(returnTo, {
                replace: true,
                state: {
                    message: isProfileFlow
                        ? "Votre nouvelle adresse e-mail a été vérifiée avec succès."
                        : "Votre adresse e-mail a été vérifiée.",
                },
            });
        } catch (requestError) {
            const responseData = requestError?.response?.data;
            const errorCode = responseData?.error || responseData?.code || responseData?.errorCode || "";
            const resolvedMessage = getAuthErrorMessage(requestError);

            setError(resolvedMessage);
            setShowResendAction(errorCode === AUTH_ERROR_CODES.EXPIRED_EMAIL_VERIFICATION_TOKEN);
        } finally {
            setLoadingVerify(false);
        }
    }, [isProfileFlow, navigate, returnTo]);

    useEffect(() => {
        if (!tokenFromQuery || autoVerifyTriggeredRef.current) {
            return;
        }

        autoVerifyTriggeredRef.current = true;
        setToken(tokenFromQuery);
        void submitVerification(tokenFromQuery);
    }, [submitVerification, tokenFromQuery]);

    function handleTokenChange(event) {
        const nextValue = event.target.value.replace(/\D/g, "").slice(0, 6);
        setToken(nextValue);
        setError("");
        setMessage("");
    }

    async function handleVerify(event) {
        event.preventDefault();
        await submitVerification(token);
    }

    async function handleResend() {
        setError("");
        setMessage("");

        if (!email) {
            setError(
                isProfileFlow
                    ? "Adresse e-mail introuvable. Retournez au profil pour relancer la demande."
                    : "Adresse e-mail introuvable. Recommencez l'inscription.",
            );
            return;
        }

        try {
            setLoadingResend(true);

            const response = await resendVerificationEmail(email);

            setMessage(response?.message || "Un nouveau code a été envoyé.");
            setShowResendAction(false);
            setToken("");
        } catch (requestError) {
            setError(getAuthErrorMessage(requestError));
        } finally {
            setLoadingResend(false);
        }
    }

    return (
        <PageLayout className="bg-gradient-to-br from-[#f4f7f8] via-white to-[#edf4f3] py-10 sm:py-14">
            <Container>
                <Card className="mx-auto w-full max-w-2xl overflow-hidden border-0 bg-white/95 p-0 shadow-[0_24px_80px_rgba(0,52,58,0.12)]">
                    <div className="h-2 bg-gradient-to-r from-[#00343a] via-[#0f5b60] to-[#fdd798]" />

                    <div className="grid gap-0 lg:grid-cols-[1.15fr_0.85fr]">
                        <div className="p-8 sm:p-10">
                            <div className="mb-8 inline-flex items-center gap-2 rounded-full border border-[#dce7e8] bg-[#f8fbfb] px-4 py-2 text-sm font-medium text-[#00343a]">
                                <ShieldCheck size={16} />
                                Vérification e-mail
                            </div>

                            <h1 className="text-3xl font-bold tracking-[-0.02em] text-[#181c1d] sm:text-4xl">
                                Vérifiez votre adresse e-mail
                            </h1>

                            <p className="mt-4 max-w-xl text-base leading-7 text-[#40484a]">
                                {isProfileFlow
                                    ? "Nous avons envoyé un code à votre nouvelle adresse e-mail. Saisissez-le pour finaliser la mise à jour."
                                    : "Nous avons envoyé un code de vérification à votre adresse e-mail."}
                            </p>

                            {email && (
                                <div className="mt-6 rounded-2xl border border-[#dce7e8] bg-[#f8fbfb] p-4">
                                    <p className="text-xs font-semibold uppercase tracking-[0.08em] text-[#6a7375]">
                                        Adresse e-mail
                                    </p>
                                    <p className="mt-1 break-words text-sm font-medium text-[#181c1d]">
                                        {email}
                                    </p>
                                </div>
                            )}

                            {message && (
                                <div className="mt-6 flex items-start gap-3 rounded-2xl border border-emerald-200 bg-emerald-50 p-4 text-emerald-900">
                                    <CheckCircle2 size={20} className="mt-0.5 shrink-0" />
                                    <p className="text-sm leading-6">{message}</p>
                                </div>
                            )}

                            {error && (
                                <div className="mt-6 flex items-start gap-3 rounded-2xl border border-rose-200 bg-rose-50 p-4 text-rose-900">
                                    <AlertCircle size={20} className="mt-0.5 shrink-0" />
                                    <p className="text-sm leading-6">{error}</p>
                                </div>
                            )}

                            <form className="mt-8 space-y-6" onSubmit={handleVerify}>
                                <Input
                                    label="Code de vérification"
                                    name="token"
                                    type="text"
                                    placeholder="123456"
                                    required
                                    value={token}
                                    onChange={handleTokenChange}
                                    disabled={loadingVerify || loadingResend}
                                    autoComplete="one-time-code"
                                    inputMode="numeric"
                                    maxLength={6}
                                    className="tracking-[0.35em] text-center text-lg font-semibold"
                                />

                                <Button
                                    type="submit"
                                    loading={loadingVerify}
                                    disabled={loadingVerify || loadingResend}
                                    className="flex w-full items-center justify-center gap-2 rounded-xl bg-[#00343a] px-4 py-3 text-sm font-semibold text-white transition-all duration-150 hover:bg-[#002b30]"
                                >
                                    Vérifier
                                    <ArrowRight size={18} strokeWidth={2} />
                                </Button>
                            </form>

                            <div className="mt-5 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                                <button
                                    type="button"
                                    onClick={handleResend}
                                    disabled={!canResend}
                                    className="inline-flex items-center gap-2 self-start rounded-xl border border-[#dce7e8] px-4 py-3 text-sm font-semibold text-[#00343a] transition-colors hover:bg-[#f8fbfb] disabled:cursor-not-allowed disabled:opacity-50"
                                >
                                    <RefreshCw size={16} />
                                    Renvoyer un code
                                </button>

                                <Link
                                    to={returnTo}
                                    className="text-sm font-medium text-[#6a7375] transition-colors hover:text-[#00343a]"
                                >
                                    {isProfileFlow ? "Retour au profil" : "Retour à la connexion"}
                                </Link>
                            </div>

                            {showResendAction && (
                                <p className="mt-3 text-sm text-[#40484a]">
                                    Votre code a expiré. Vous pouvez en demander un nouveau.
                                </p>
                            )}
                        </div>

                        <div className="flex items-stretch bg-[#00343a] p-8 sm:p-10">
                            <div className="flex w-full flex-col justify-between rounded-3xl border border-white/10 bg-white/5 p-6 text-white backdrop-blur-sm">
                                <div>
                                    <div className="mb-4 inline-flex h-12 w-12 items-center justify-center rounded-2xl bg-[#fdd798] text-[#00343a]">
                                        <Mail size={22} />
                                    </div>
                                    <h2 className="text-2xl font-bold leading-tight">
                                        Sécurisez votre compte en quelques secondes.
                                    </h2>
                                    <p className="mt-3 text-sm leading-6 text-white/80">
                                        Saisissez le code à 6 chiffres pour continuer sur TerangaLink.
                                    </p>
                                </div>

                                <div className="mt-10 grid gap-3 text-sm text-white/85">
                                    <div className="rounded-2xl border border-white/10 bg-white/5 px-4 py-3">
                                        Le code arrive sur l’adresse utilisée pour la vérification.
                                    </div>
                                    <div className="rounded-2xl border border-white/10 bg-white/5 px-4 py-3">
                                        Les codes expirés peuvent être renvoyés à tout moment.
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </Card>
            </Container>
        </PageLayout>
    );
}

export default VerifyEmailPage;



