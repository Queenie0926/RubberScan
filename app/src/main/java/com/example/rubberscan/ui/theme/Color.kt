package com.example.rubberscan.ui.theme

import androidx.compose.ui.graphics.Color

// ── Brand greens ──────────────────────────────────────────────
val GreenDark     = Color(0xFF1B5E20)   // primary header, buttons, key text
val GreenDeep     = Color(0xFF2E7D32)   // nav selected, vein drawing
val GreenMid      = Color(0xFF388E3C)   // disease-risk status card
val GreenAccent   = Color(0xFF4CAF50)   // live indicator, scan overlay, processing ring
val GreenSoft     = Color(0xFF66BB6A)   // avatar gradient start
val GreenPale1    = Color(0xFF81C784)   // processing border, history chart
val GreenPale2    = Color(0xFFA5D6A7)   // leaf illustration, disease guide icon
val GreenPale3    = Color(0xFFC8E6C9)   // illustration fill, history chart bar
val GreenLight    = Color(0xFFE8F5E9)   // chip / badge backgrounds
val GreenGradient = Color(0xFFB2F2C2)   // gradient bottom in Welcome / Login

// ── Page & card backgrounds ───────────────────────────────────
val PageBg        = Color(0xFFF1F8F1)   // shared page background across all screens
val CardBg        = Color(0xFFFFFFFF)   // card / surface white

// ── Oranges / Amber ───────────────────────────────────────────
val OrangeDark    = Color(0xFFE65100)   // temperature icon, warning primary
val OrangeMid     = Color(0xFFFF9800)   // PLFD badge, progress dots
val AmberDark     = Color(0xFFF9A825)   // mild severity, early-warning icon
val OrangeLight   = Color(0xFFFFF3E0)   // orange element backgrounds
val OrangeLighter = Color(0xFFFFE0B2)   // result badge bg for mild
val AmberLight    = Color(0xFFFFFDE7)   // mild severity bg
val AmberLightest = Color(0xFFFFF8E1)   // early-warning alert card bg
val AmberBorder   = Color(0xFFFFD54F)   // early-warning card border
val AmberBg       = Color(0xFFFFFDE7)   // amber chip background

// ── Reds ──────────────────────────────────────────────────────
val RedDark       = Color(0xFFC62828)   // error, disconnect, severe disease
val RedMid        = Color(0xFFEF5350)   // sign-out button content color
val ErrorRed      = Color(0xFFB00020)   // form validation error text
val RedLight      = Color(0xFFFFEBEE)   // error / disconnected background
val RedLighter    = Color(0xFFFFCDD2)   // sign-out button border

// ── Blues ─────────────────────────────────────────────────────
val BlueDark      = Color(0xFF0D47A1)   // humidity icon, BLE scanning state
val BlueIndigo    = Color(0xFF3949AB)   // settings – notifications icon
val BlueLighter   = Color(0xFF64B5F6)   // scan screen humidity chip
val BluePale      = Color(0xFF90CAF9)   // onboarding card border
val BlueLight     = Color(0xFFE3F2FD)   // humidity element backgrounds

// ── Purple ────────────────────────────────────────────────────
val PurpleDark    = Color(0xFF6A1B9A)   // recommendation type color
val PurpleLight   = Color(0xFFF3E5F5)   // recommendation type background

// ── Teal ──────────────────────────────────────────────────────
val TealDark      = Color(0xFF00695C)   // settings – theme / appearance icon
val TealLight     = Color(0xFFE0F2F1)   // teal icon background

// ── Slate ─────────────────────────────────────────────────────
val SlateGray      = Color(0xFF546E7A)  // secondary icon tint (settings, BLE)
val SlateCharcoal  = Color(0xFF37474F)  // BLE idle / scan-stop state
val SlateGrayLight = Color(0xFFECEFF1)  // slate icon background

// ── Text ──────────────────────────────────────────────────────
val TextPrimary   = Color(0xFF1C1C1C)   // main body text
val TextSecondary = Color(0xFF424242)   // section labels, card titles
val TextBody      = Color(0xFF555555)   // disease symptoms, step descriptions
val TextDark      = Color(0xFF4A4A4A)   // BLE subtitles, status descriptions
val TextMedium    = Color(0xFF666666)   // result desc, history sensor readings
val TextMuted     = Color(0xFF9E9E9E)   // captions, secondary labels
val TextMuted2    = Color(0xFF9CA3AF)   // lighter muted (profile, settings, processing)
val TextDisabled  = Color(0xFFBDBDBD)   // disabled / no-camera text
val AuthHint      = Color(0xFFAAAAAA)   // login field placeholder

// ── Icons ─────────────────────────────────────────────────────
val IconInactive  = Color(0xFFB0B0B0)   // unselected tab icons / filters
val IconChevron   = Color(0xFFD1D5DB)   // chevron arrows in list rows

// ── Borders & surfaces ────────────────────────────────────────
val BorderLight   = Color(0xFFD1D5DB)   // toggle unchecked track, subtle borders
val BorderMedium  = Color(0xFFE0E0E0)   // dividers, Google-button border
val BorderGray    = Color(0xFFF0F0F0)   // card internal dividers
val FieldBg       = Color(0xFFF5F5F5)   // text-field background
val SurfaceGray   = Color(0xFFF3F4F6)   // severity inactive row
val DividerLight  = Color(0xFFF8F8F8)   // recommendation steps background
val SurfaceLight  = Color(0xFFFAFAFA)   // history-detail image area
val ProcessBorder = Color(0xFFDCEEDC)   // processing card border
val YellowSoft    = Color(0xFFFFF176)   // environmental risk gauge – low-moderate
val OrangeGauge   = Color(0xFFFFCC80)   // environmental risk gauge – moderate
val RedGauge      = Color(0xFFEF9A9A)   // environmental risk gauge – high
