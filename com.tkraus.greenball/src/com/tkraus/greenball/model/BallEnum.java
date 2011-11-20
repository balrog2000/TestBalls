package com.tkraus.greenball.model;

import com.tkraus.greenball.R;

public enum BallEnum {
	GREY(R.drawable.ball_grey, false), RED(R.drawable.ball_red, false), YELLOW(
			R.drawable.ball_yellow, false), GREEN(R.drawable.ball_green, false), GREY_ANIME(
			R.drawable.ball_grey, true), RED_ANIME(R.drawable.ball_red, true), YELLOW_ANIME(
			R.drawable.ball_yellow, true), GREEN_ANIME(R.drawable.ball_green,
			true);

	private final int ballResourceId;
	private final boolean animated;

	BallEnum(int ballResourceId, boolean animated) {
		this.ballResourceId = ballResourceId;
		this.animated = animated;
	}

	public static BallEnum fromColor(String color) {
		if (color.equals("disabled")) {
			return BallEnum.GREY;
		} else if (color.equals("disabled_anime")) {
			return BallEnum.GREY_ANIME;
		} else {
			color = color.replaceFirst("blue", "green");
			return BallEnum.valueOf(color.toUpperCase());
		}
	}

	public int getBallResourceId() {
		return ballResourceId;
	}

	public boolean isAnimated() {
		return animated;
	}
}
