import { Outlet } from "react-router";
import { AuthProvider } from "~/contexts/AuthContext";

export default function AuthLayout() {
  return (
    <AuthProvider>
      <Outlet />
    </AuthProvider>
  );
}
