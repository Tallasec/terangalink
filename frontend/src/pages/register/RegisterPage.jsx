// Hooks React
import { useState } from "react";

// Navigation
import { Link, useNavigate } from "react-router-dom";

// Icons
import { ArrowRight, GraduationCap, Handshake } from "lucide-react";

// Services auth
import { getAuthErrorMessage, registerUser } from "../../services/auth/authService";
import { PENDING_VERIFICATION_EMAIL_KEY } from "../../types/auth";

// Layout
import PageLayout from "../../components/common/layout/PageLayout";
import Container from "../../components/common/layout/Container";
import Card from "../../components/common/ui/Card";

// Components
import Logo from "../../components/common/Logo";
import Button from "../../components/common/ui/Button";
import AuthField from "../../components/auth/AuthField";
import AuthSplitLayout from "../../components/auth/AuthSplitLayout";

// Image
import MainImage from "../../assets/img/Main3.PNG";

function RegisterPage() {
    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        firstName: "",
        lastName: "",
        email: "",
        password: "",
        confirmPassword: "",
        university: "",
        fieldOfStudy: "",
        city: "",
    });

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    function handleChange(event) {
        const { name, value } = event.target;

        setFormData((previousData) => ({
            ...previousData,
            [name]: value,
        }));

        if (error) {
            setError("");
        }
    }

    async function handleSubmit(event) {
        event.preventDefault();
        setError("");

        if (formData.password !== formData.confirmPassword) {
            setError("Les mots de passe ne correspondent pas.");
            return;
        }

        try {
            setLoading(true);

            const registerData = { ...formData };
            delete registerData.confirmPassword;
            const response = await registerUser(registerData);

            sessionStorage.setItem(PENDING_VERIFICATION_EMAIL_KEY, registerData.email);

            navigate("/verify-email", {
                state: {
                    email: registerData.email,
                    message:
                        response?.message ||
                        "Compte créé avec succès. Vérifiez votre adresse e-mail.",
                },
            });
        } catch (requestError) {
            setError(getAuthErrorMessage(requestError, "Le serveur est inaccessible."));
        } finally {
            setLoading(false);
        }
    }

    return (
        <PageLayout className="py-12 sm:py-16">
            <Container>
                <Card className="mx-auto w-full max-w-[1000px] overflow-hidden rounded-xl p-0 ring-0">
                    <AuthSplitLayout>
                        <div className="relative hidden min-h-[720px] overflow-hidden bg-[#00343a] md:block">
                            <img
                                src={MainImage}
                                alt=""
                                aria-hidden="true"
                                className="absolute inset-0 h-full w-full object-cover opacity-60"
                            />

                            <div className="relative z-10 flex h-full flex-col justify-between p-12 text-white">
                                <div>
                                    <Logo className="text-[28px] leading-[32px] text-white" />
                                    <h2 className="mt-6 max-w-md text-[48px] font-bold leading-[56px] tracking-[-0.02em] text-white">
                                        Bienvenue chez vous, même loin de chez vous.
                                    </h2>
                                </div>

                                <div className="space-y-6">
                                    <div className="flex items-start gap-4">
                                        <Handshake
                                            size={24}
                                            strokeWidth={2}
                                            className="mt-0.5 text-[#fdd798]"
                                        />
                                        <p className="text-[16px] leading-[24px] text-white/90">
                                            Rejoignez un espace fondé sur la valeur sénégalaise de la Teranga.
                                        </p>
                                    </div>

                                    <div className="flex items-start gap-4">
                                        <GraduationCap
                                            size={24}
                                            strokeWidth={2}
                                            className="mt-0.5 text-[#fdd798]"
                                        />
                                        <p className="text-[16px] leading-[24px] text-white/90">
                                            Naviguez dans votre parcours académique en France avec des guides de confiance.
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div className="flex flex-col justify-center p-8 md:p-12 lg:p-16">
                            <div className="mb-10 text-center md:text-left">
                                <h1 className="text-[32px] font-bold leading-[40px] text-[#181c1d]">
                                    Créez votre compte
                                </h1>
                                <p className="mt-2 text-[16px] leading-[24px] text-[#40484a]">
                                    Rejoignez le réseau des étudiants sénégalais en France.
                                </p>
                            </div>

                            {error && (
                                <div
                                    className="mb-6 flex items-start gap-3 rounded-2xl border border-rose-200 bg-rose-50 p-4 text-rose-900"
                                    role="alert"
                                >
                                    <span className="material-symbols-outlined mt-0.5 text-[20px]">
                                        error
                                    </span>
                                    <p className="text-sm leading-6">{error}</p>
                                </div>
                            )}

                            <form className="space-y-6" onSubmit={handleSubmit}>
                                <div className="grid gap-6 sm:grid-cols-2">
                                    <AuthField
                                        label="Prénom"
                                        name="firstName"
                                        placeholder="Mor talla"
                                        icon="user"
                                        required
                                        autoComplete="given-name"
                                        value={formData.firstName}
                                        onChange={handleChange}
                                    />

                                    <AuthField
                                        label="Nom"
                                        name="lastName"
                                        placeholder="Seck"
                                        icon="user"
                                        required
                                        autoComplete="family-name"
                                        value={formData.lastName}
                                        onChange={handleChange}
                                    />
                                </div>

                                <AuthField
                                    label="Email"
                                    type="email"
                                    name="email"
                                    placeholder="mortalla@teranga.fr"
                                    icon="mail"
                                    required
                                    autoComplete="email"
                                    value={formData.email}
                                    onChange={handleChange}
                                />

                                <AuthField
                                    label="Mot de passe"
                                    type="password"
                                    name="password"
                                    placeholder="••••••••"
                                    icon="lock"
                                    togglePassword
                                    required
                                    autoComplete="new-password"
                                    value={formData.password}
                                    onChange={handleChange}
                                />

                                <AuthField
                                    label="Confirmation du mot de passe"
                                    type="password"
                                    name="confirmPassword"
                                    placeholder="••••••••"
                                    icon="lock"
                                    togglePassword
                                    required
                                    autoComplete="new-password"
                                    value={formData.confirmPassword}
                                    onChange={handleChange}
                                />

                                <div className="grid gap-6 sm:grid-cols-2">
                                    <AuthField
                                        label="Université"
                                        name="university"
                                        placeholder="Aix-Marseille Université"
                                        icon="school"
                                        required
                                        value={formData.university}
                                        onChange={handleChange}
                                    />

                                    <AuthField
                                        label="Domaine d'études"
                                        name="fieldOfStudy"
                                        placeholder="Informatique"
                                        icon="school"
                                        required
                                        value={formData.fieldOfStudy}
                                        onChange={handleChange}
                                    />
                                </div>

                                <AuthField
                                    label="Ville"
                                    name="city"
                                    placeholder="Marseille"
                                    icon="map-pin"
                                    required
                                    value={formData.city}
                                    onChange={handleChange}
                                />

                                <Button
                                    type="submit"
                                    disabled={loading}
                                    className="flex w-full items-center justify-center gap-2 rounded-lg bg-[#00343a] px-4 py-4 text-[12px] font-semibold uppercase tracking-[0.05em] text-white transition-all duration-150 hover:scale-[0.99] hover:bg-[#002b30]"
                                >
                                    {loading ? (
                                        "Création..."
                                    ) : (
                                        <>
                                            Créer un compte
                                            <ArrowRight size={20} strokeWidth={2} />
                                        </>
                                    )}
                                </Button>
                            </form>

                            <div className="mt-8 border-t border-[#bfc8ca] pt-8 text-center">
                                <p className="text-[16px] leading-[24px] text-[#40484a]">
                                    Déjà un compte ?
                                    <Link
                                        to="/login"
                                        className="ml-1 font-semibold text-[#00343a] hover:underline"
                                    >
                                        Se connecter
                                    </Link>
                                </p>
                            </div>
                        </div>
                    </AuthSplitLayout>
                </Card>
            </Container>
        </PageLayout>
    );
}

export default RegisterPage;
