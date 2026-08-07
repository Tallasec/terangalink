import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";

import { getAuthErrorMessage, loginUser } from "../../services/auth/authService";
import { setAccessToken } from "../../services/auth/authStorage";

import PageLayout from "../../components/common/layout/PageLayout";
import Container from "../../components/common/layout/Container";
import Card from "../../components/common/ui/Card";
import Logo from "../../components/common/Logo";
import Input from "../../components/common/ui/Input";
import Button from "../../components/common/ui/Button";

function LoginPage() {
    const location = useLocation();
    const navigate = useNavigate();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState(location.state?.message || "");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    async function handleSubmit(event) {
        event.preventDefault();
        setError("");
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
            setError(getAuthErrorMessage(requestError, "Le serveur est inaccessible."));
        } finally {
            setLoading(false);
        }
    }

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
                            {error}
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
                            disabled={loading}
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
                            disabled={loading}
                        />

                        <Button type="submit" className="w-full" loading={loading} disabled={loading}>
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
