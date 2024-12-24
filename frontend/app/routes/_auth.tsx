import { Outlet } from "@remix-run/react";
import { AuthProvider } from "~/contexts/AuthContext";

export default function AuthLayout() {
  return (
    <AuthProvider>
      <Outlet />
    </AuthProvider>
  );
}
