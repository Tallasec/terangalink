export function getUserFullName(user) {
  if (!user) {
    return "";
  }

  const parts = [user.firstName, user.lastName].filter(
    (value) => typeof value === "string" && value.trim() !== "",
  );

  return parts.join(" ").trim();
}

export function getUserInitials(user) {
  const fullName = getUserFullName(user);

  if (!fullName) {
    return "TL";
  }

  return fullName
    .split(" ")
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase())
    .join("");
}
