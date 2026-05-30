import Link from "next/link";
import type { ReactNode } from "react";

export default function Layout({ children }: { children: ReactNode }) {
  return (
    <>
      <header className="site-header">
        <div className="site-header__inner">
          <Link href="/" className="brand">
            <span className="brand__mark">손맛 · Son-Mat</span>
            <span className="brand__sub">Korean recipes from what you have</span>
          </Link>
        </div>
      </header>
      <main className="container">{children}</main>
      <footer className="footer">
        손맛 (son-mat) — “the taste of one’s hands.” Cook Korean with what’s in your kitchen.
      </footer>
    </>
  );
}
