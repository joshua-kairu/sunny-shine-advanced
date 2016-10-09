/*
 *  Sunshine
 *
 * A simple weather app
 *
 * Copyright (C) 2016 Kairu Joshua Wambugu
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */

package com.jlt.sunshine.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

import com.jlt.sunshine.R;

/**
 * A {@link android.view.View} that shows wind direction and speed.
 * */
// begin class WindDirectionSpeedView
public class WindDirectionAndSpeedView extends View {

    /* CONSTANTS */

    /* Integers */

    /** The default difference between the radii of the outer and inner circles in dp.  */
    private static final int DEFAULT_RADIUS_DIFFERENCE_DP = 16;

    /**
     * The space between the north indicator and the outer circle.
     * It should be equal to the space between the north indicator and the inner circle. */
    private static final int NORTH_INDICATOR_VERTICAL_PADDING_DP = 4;

    /** The default distance from the compass to the speed text in dp. */
    private static final int DEFAULT_SPEED_TEXT_LEFT_PADDING_DP = 8;

    /** The largest possible speed text. Used to calculate minimum height and width. */
    public static final String LARGEST_SPEED_TEXT = "300 km/H"; // an assumption, of course.

    /* VARIABLES */

    /* Matrices */

    private Matrix mArrowPathRotationMatrix; // ditto

    /* Paints */

    private Paint mCirclePaint; // ditto
    private Paint mNorthIndicatorPaint; // ditto
    private Paint mArrowPaint; // ditto
    private Paint mSpeedPaint; // ditto

    /* Paths */

    private Path mArrowPath; // ditto

    /* Primitives */

    private float mArrowAngle; // ditto
    private float mArrowAngleToDraw; // ditto, used to draw the arrow during animation
    private float mRadiusDifference; // ditto
    private float mNorthIndicatorStrokeWidth; // ditto
    private float mSpeedTextLeftX; // the left coordinate where we'll finally draw the speed text
    private float mSpeedTextLeftXToDraw; // ditto, used for animation
    private float mSpeedStrokeWidth; // ditto

    private int mOuterCircleColor; // ditto
    private int mInnerCircleColor; // ditto
    private int mNorthIndicatorColor; //ditto
    private int mArrowColor; // ditto
    private int mAnimationDuration; // ditto
    private int mSpeedColor; // ditto
    private int mSpeedTextAlphaToDraw; // ditto, used for animation

    private long mAnimationDurationToUse; // ditto, the actual animation time we will use.
                                          // It ensures all items getting animated
                                          // move at a constant speed.

    /* RectFs */

    private RectF mArrowBottomArcRectF; // rectangle to hold the arc used in the arrow

    /* Strings */

    private String mNorthIndicatorText; //ditto

    private String mSpeedText; // ditto

    private String mWindDirectionAndSpeedTextForAccessibility; // ditto, for accessibility

    /* Text Paints */

    private TextPaint mTextPaint; // used to measure the text width

    /* Value Animators */

    private ValueAnimator mArrowValueAnimator; // to animate the arrow
    private ValueAnimator mSpeedAlphaValueAnimator; // to animate the speed's alpha
    private ValueAnimator mSpeedTranslationXValueAnimator; // to animate the speed's translationX

    /* CONSTRUCTOR */

    // begin default constructor for XML
    public WindDirectionAndSpeedView( Context context, AttributeSet attrs ) {

        // 0. super stuff
        // 1. initialize things
        // 2. put accessibility things in

        // 0. super stuff

        super( context, attrs );

        // 1. initialize things

        initView( context, attrs );

        // 2. put accessibility things in

        installAccessibilityDelegate();

    } // end default constructor for XML

    /* METHODS */
    
    /* Getters and Setters */

    // getter for the outer circle color
    public int getOuterCircleColor() {
        return mOuterCircleColor;
    }

    // setter for the outer circle color
    public void setOuterCircleColor( int outerCircleColor ) {

        this.mOuterCircleColor = outerCircleColor;

        // refresh the layout
        invalidate();
        requestLayout();

    }

    // getter for the inner circle color
    public int getInnerCircleColor() {
        return mInnerCircleColor;
    }

    // setter for the inner circle color
    public void setInnerCircleColor( int innerCircleColor ) {

        this.mInnerCircleColor = innerCircleColor;

        // refresh the layout
        invalidate();
        requestLayout();

    }

    // getter for the north indicator color
    public int getNorthIndicatorColor() {
        return mNorthIndicatorColor;
    }

    // setter for the north indicator color
    public void setNorthIndicatorColor( int northIndicatorColor ) {

        this.mNorthIndicatorColor = northIndicatorColor;

        // refresh the layout
        invalidate();
        requestLayout();

    }

    // getter for the arrow color
    public int getArrowColor() {
        return mArrowColor;
    }

    // setter for the arrow color
    public void setArrowColor( int arrowColor ) {

        this.mArrowColor = arrowColor;

        // refresh the layout
        invalidate();
        requestLayout();

    }

    // getter for the arrow angle
    public float getArrowAngle() {
        return mArrowAngle;
    }

    // begin setter for the arrow angle
    public void setArrowAngle( float newArrowAngle ) {

        // 0. take care of any invalid initialization
        // 0a. if the new angle is less than zero then the member one should be zero
        // 0b. if the new angle is greater than 360 then the member one should be within 360
        // 0c. otherwise everything is okay, the member angle should have the value of the new angle
        // 1. animate the new angle
        // 2. send the accessibility event for arrow angle being changed
        // last. refresh the layout

        // 0. take care of any invalid initialization

        // 0a. if the new angle is less than zero then the member one should be zero

        if ( newArrowAngle < 0f ) { mArrowAngle = 0; }

        // 0b. if the new angle is greater than 360 then the member one should be within 360

        else if ( newArrowAngle > 360f ) { mArrowAngle = 360 - newArrowAngle % 360; }

        // 0c. otherwise everything is okay, the member angle should have the value of the new angle

        else { mArrowAngle = newArrowAngle; }

        // 1. animate the new angle

        // we'll animate from 0 degrees to the arrow angle's degrees
        animateArrowRotation( 0, getArrowAngle() );

        // 2. send the accessibility event for arrow angle being changed

        AccessibilityManager accessibilityManager =
                ( AccessibilityManager ) getContext().getSystemService( Context.ACCESSIBILITY_SERVICE );

        if ( accessibilityManager.isEnabled() == true ) {
            sendAccessibilityEvent( AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
        }

        // last. refresh the layout

        invalidate();
        requestLayout();
        
    } // end setter for the arrow angle

    // getter for the animation duration
    public int getAnimationDuration() {
        return mAnimationDuration;
    }

    // begin setter for the animation duration
    public void setAnimationDuration( int animationDuration ) {

        this.mAnimationDuration = animationDuration;

        // refresh the layout
        invalidate();
        requestLayout();

    } // end setter for the arrow animation duration

    // getter for the north indicator text
    public String getNorthIndicatorText() {
        return mNorthIndicatorText;
    }

    // setter for the north indicator text
    public void setNorthIndicatorText( String northIndicatorText ) {

        this.mNorthIndicatorText = northIndicatorText;

        // refresh the layout
        invalidate();
        requestLayout();

    }

    // getter for the north indicator stroke width
    public float getNorthIndicatorStrokeWidth() {
        return mNorthIndicatorStrokeWidth;
    }

    // setter for the north indicator stroke width
    public void setNorthIndicatorStrokeWidth( float northIndicatorStrokeWidth ) {

        this.mNorthIndicatorStrokeWidth = northIndicatorStrokeWidth;

        // refresh the layout
        invalidate();
        requestLayout();

    }

    // getter for the radius difference
    public float getRadiusDifference() {
        return mRadiusDifference;
    }

    // setter for the radius difference
    public void setRadiusDifference( float radiusDifference ) {

        this.mRadiusDifference = radiusDifference;

        // refresh the layout
        invalidate();
        requestLayout();

    }

    // getter for the speed text
    public String getSpeedText() {
        return mSpeedText;
    }

    // begin setter for the speed text
    public void setSpeedText( String speedText ) {

        // 0. initialize the member variable
        // 1. animate the text
        // 2. send the accessibility event for arrow angle being changed
        // last. refresh the layout

        // 0. initialize the member variable

        this.mSpeedText = speedText;

        // 1. animate the text

        animateSpeedText();

        // 2. send the accessibility event for arrow angle being changed

        AccessibilityManager accessibilityManager =
                ( AccessibilityManager ) getContext().getSystemService( Context.ACCESSIBILITY_SERVICE );

        if ( accessibilityManager.isEnabled() == true ) {
            sendAccessibilityEvent( AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED );
        }

        // last. refresh the layout

        invalidate();
        requestLayout();

    } // end setter for the speed text

    /* Overrides */

    @Override
    /**
     * Measure the view and its content to determine the measured width and the measured height.
     * */
    // begin onMeasure
    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ) {

        // 0. try for a width based on our minimum
        // 1. whatever the width, ask for a height that would let the view get as big as possible
        // 2. use the gotten width and height

        // 0. try for a width based on our minimum

        int minimumWidth = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();

        int widthToUse = -1;

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
            // reconcile a desired size and state, with constraints imposed by a MeasureSpec
            widthToUse = resolveSizeAndState( minimumWidth, widthMeasureSpec, 1 );
        }

        else { widthToUse = minimumWidth; }

        // 1. whatever the width, ask for a height that would let the view get as big as possible

        // getSize - Extracts the size from the supplied measure specification.
        int minimumHeight = getSuggestedMinimumWidth() + getPaddingBottom() + getPaddingTop();

        int heightToUse = -1;

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
            heightToUse = resolveSizeAndState( getSuggestedMinimumWidth(), heightMeasureSpec, 0 );
        }

        else { heightToUse = minimumHeight; }

        // 2. use the gotten width and height

        // Must be called by onMeasure(int, int) to store the measured width and measured height.
        // Failing to do so will trigger an exception at measurement time.
        setMeasuredDimension( widthToUse, heightToUse );

    } // end onMeasure

    /**
     * Returns the suggested minimum height that the view should use.
     *
     * This returns the maximum of the view's minimum height and
     * the background's minimum height (getMinimumHeight()).
     *
     * In our case, I think the view should be at least 4 times as high as the largest speed text.
     * */
    @Override
    // begin getSuggestedMinimumHeight
    protected int getSuggestedMinimumHeight() {

        return ( int ) ( getTextLength( LARGEST_SPEED_TEXT, mTextPaint ) * 4 );

    } // end getSuggestedMinimumHeight

    /**
     * Returns the suggested minimum height that the view should use.
     *
     * This returns the maximum of the view's minimum height and
     * the background's minimum height (getMinimumHeight()).
     *
     * In our case, I think the view should be at least 4 times as high as the largest speed text.
     * */
    @Override
    // begin getSuggestedMinimumWidth
    protected int getSuggestedMinimumWidth() {

        return ( int ) ( getTextLength( LARGEST_SPEED_TEXT, mTextPaint ) * 4 );

    } // end getSuggestedMinimumWidth

    @Override
    // begin onSizeChanged
    protected void onSizeChanged( int currentWidth, int currentHeight, int oldWidth, int oldHeight ) {

        // 0. super stuff
        // 1. get the value for the left x of the speed text
        // 1a. it is the x origin plus
        // 1b. the outer circle's diameter plus
        // 1c. the default speed text left padding
        // 2. the speed's left x to draw should be to the left of the value in 1
        // by the length of the speed text

        // 0. super stuff

        super.onSizeChanged( currentWidth, currentHeight, oldWidth, oldHeight );

        // 1. get the value for the left x of the speed text

        // 1a. it is the x origin plus

        // 1b. the outer circle's diameter plus

        // copied from onDraw ;-)

        int viewHalfHeight = getTotalHeight() / 2;

        int viewHalfWidth = getTotalWidth() / 2;

        int outerRadius = -1;

        // we want the circle to stay in its half of the view

        if ( getTotalHeight() < viewHalfWidth ) { outerRadius = viewHalfHeight; }
        else if ( viewHalfWidth < getTotalHeight() ) { outerRadius = viewHalfWidth / 2; }

        // 1c. the default speed text left padding

        int outerDiameter = outerRadius * 2;

        mSpeedTextLeftX = getOriginX( outerRadius ) + outerDiameter +
                dpToPx( DEFAULT_SPEED_TEXT_LEFT_PADDING_DP );

        // 2. the speed's left x to draw should be to the left of the value in 1
        // by the length of the speed text

        mSpeedTextLeftXToDraw = mSpeedTextLeftX - getTextLength( mSpeedText, mTextPaint );

        // 3. animate the arrow rotation

        // from 0 degrees to the current angle
        animateArrowRotation( 0, mArrowAngle );

        // 4. animate the speed text

        animateSpeedText();

    } // end onSizeChanged

    @Override
    // begin onDraw
    protected void onDraw( Canvas canvas ) {

        // 0. super stuff
        // 1. draw the outer circle
        // 1a. radius should be half the smaller of the lengths
        // 1b. use the outer circle color
        // 1c. draw
        // 2. draw the inner circle
        // 2a. radius should be less than the outer circle by the radius difference
        // 2b. use the inner circle color
        // 2c. draw
        // 3. color the radius difference space
        // 4. write the north indicator
        // 4a. should be as tall as the radius difference and
        // the individual vertical indicator padding can allow
        // 4b. should be on the top of the circle
        // 4c. should be on the middle of the circle
        // 4d. should use the right color
        // 4e. should use the right boldness
        // 4f. should be drawn
        // 5. draw the arrow facing the north indicator
        // 5a. draw point A facing the indicator
        // 5b. draw point B to the left of A
        // 5c. draw point C to the right of A and B
        // 5d. join the points
        // 5e. color the arrow
        // 5last. draw the arrow
        // 6. rotate the arrow as needed
        // 7. put a space (of 16 dp) between speed text and compass
        // 8. draw speed text
        // 8a. should use the correct color
        // 8b. should use the correct boldness
        // 8c. should be in the correct position
        // 8c1. height should be about similar to half the inner circle radius,
        // just as an on-the-fly rule of thumb
        // 8d. should have the correct alpha
        // 8e. should be drawn
        // last. reset
        // lasta. the arrow path
        // lastb. the rotation matrix

        // 0. super stuff

        super.onDraw( canvas );

        // FOR THE SPEED TEXT TO APPEAR BEHIND THE COMPASS,
        // THE TEXT NEEDS TO BE DRAWN FIRST
        // BECAUSE OF THE PAINTER'S ALGORITHM.
        // ALL VARIABLES NEEDED FOR DRAWING THE SPEED TEXT ARE DECLARED HERE BELOW,
        // EVEN THOUGH THEY WILL BE USED TO DRAW THE COMPASS LATER ON.

        int viewHalfHeight = getTotalHeight() / 2;

        int viewHalfWidth = getTotalWidth() / 2;

        int outerRadius = -1;

        // we want the circle to stay in its half of the view

        if ( getTotalHeight() < viewHalfWidth ) { outerRadius = viewHalfHeight; }
        else if ( viewHalfWidth < getTotalHeight() ) { outerRadius = viewHalfWidth / 2; }

        float innerRadius = outerRadius - mRadiusDifference;

        float viewQuarterWidth = viewHalfWidth / 2;

        float circleCenterX = viewQuarterWidth, circleCenterY = viewHalfHeight;

        // 8. draw speed text

        // 8a. should use the correct color

        mSpeedPaint.setColor( mSpeedColor );

        // 8b. should use the correct boldness

        mSpeedPaint.setStrokeWidth( mSpeedStrokeWidth );
        mSpeedPaint.setStyle( Paint.Style.FILL );

        // 8c. should be in the correct position

        // 8c1. height should be about similar to half the inner circle radius,
        // just as an on-the-fly rule of thumb

        float speedTextSize = innerRadius / 2.0f;

        mSpeedPaint.setTextSize( speedTextSize );

        float speedTextHeight = Math.abs(
                mSpeedPaint.getFontMetrics().bottom + mSpeedPaint.getFontMetrics().top
        );

        float speedTextY = viewHalfHeight + speedTextHeight / 2.0f;

        // 8d. should have the correct alpha

        mSpeedPaint.setAlpha( mSpeedTextAlphaToDraw );

        // 8e. should be drawn

        canvas.drawText( mSpeedText, mSpeedTextLeftXToDraw, speedTextY, mSpeedPaint );

        // 1. draw the outer circle

        // 1a. radius should be half the smaller of the lengths -> these values are gotten above
        // when we are drawing the speed text.

        // 1b. use the outer circle color

        mCirclePaint.setColor( mOuterCircleColor );

        // 1c. draw

        canvas.drawCircle( circleCenterX, circleCenterY, outerRadius, mCirclePaint );

        // 2. draw the inner circle

        // 2a. radius should be less than the outer circle by the radius difference -> done above
        // for use in speed text drawing.

        // 2b. use the inner circle color

        mCirclePaint.setColor( mInnerCircleColor );

        // 2c. draw

        canvas.drawCircle( circleCenterX, circleCenterY, innerRadius, mCirclePaint );

        // 3. color the radius difference space -> already done by filling the circles

        // 4. write the north indicator

        // 4a. should be as tall as the radius difference and
        // the individual vertical indicator padding can allow

        float textHeight = mRadiusDifference - ( dpToPx( NORTH_INDICATOR_VERTICAL_PADDING_DP ) * 2 );
        mNorthIndicatorPaint.setTextSize( textHeight );

        // 4b. should be on the top of the circle

        // thanks to http://www.programcreek.com/java-api-examples/index.php?source_dir=andbase-master/AndBase/src/com/ab/util/AbGraphicUtil.java
        // for the TextPaint idea
        float northIndicatorHeight = mTextPaint.getTextSize();

        float northIndicatorWidth = getTextLength( mNorthIndicatorText, mTextPaint );

        float northIndicatorY =
                ( viewHalfHeight - outerRadius ) + // center it vertically on top of the outer circle
                        mRadiusDifference / 2 + // put its baseline halfway the distance from the outer circle to the inner circle
                        ( northIndicatorHeight / 2 ) // put its baseline below the halfway by half the text height
                ; // thus putting the indicator right in the middle of the circle

        // 4c. should be on the middle of the circle

        float northIndicatorX = circleCenterX - northIndicatorWidth / 2.0f;

        // 4d. should use the right color

        mNorthIndicatorPaint.setColor( mNorthIndicatorColor );

        mNorthIndicatorPaint.setStyle( Paint.Style.STROKE );

        // 4e. should use the right boldness

        mNorthIndicatorPaint.setStrokeWidth( mNorthIndicatorStrokeWidth );

        // 4f. should be drawn

        canvas.drawText( mNorthIndicatorText, northIndicatorX, northIndicatorY, mNorthIndicatorPaint );

        // 5. draw the arrow facing the north indicator

        // 5a. draw point A facing the indicator

        float aX = circleCenterX,
              aY = circleCenterY - innerRadius;

        // 5b. draw point B to the left of A

        float bX = circleCenterX - mRadiusDifference,
              bY = circleCenterY + northIndicatorHeight;

        // 5c. draw point C to the right of A and B

        float cX = bX + ( mRadiusDifference * 2 ), cY = bY;

        // 5d. join the points

        mArrowPath.moveTo( aX, aY );
        mArrowPath.lineTo( bX, bY );
        mArrowBottomArcRectF.left = bX;
        mArrowBottomArcRectF.top = bY - northIndicatorHeight;
        mArrowBottomArcRectF.right = cX;
        mArrowBottomArcRectF.bottom = cY + northIndicatorHeight;

        // 0 starts at right
        // -180 starts at left
        mArrowPath.arcTo( mArrowBottomArcRectF, -180f, 180f );

        mArrowPath.close();

        // 5e. color the arrow

        mArrowPaint.setColor( mArrowColor );
        mArrowPaint.setStyle( Paint.Style.FILL );

        // 5last. draw the arrow -> drawn when rotated at 6

        // 6. rotate the arrow as needed

        // http://stackoverflow.com/questions/6763231/draw-rotated-path-at-particular-point
        mArrowPathRotationMatrix.postRotate( mArrowAngleToDraw, circleCenterX, circleCenterY );

        mArrowPath.transform( mArrowPathRotationMatrix );

        canvas.drawPath( mArrowPath, mArrowPaint );

        // last. reset

        // lasta. the arrow path

        mArrowPath.reset();

        // lastb. the rotation matrix

        // http://android-er.blogspot.co.ke/2014/06/rotate-path-with-matrix.html, MyShape.onDraw
        mArrowPathRotationMatrix.reset();

    } // end onDraw

    /**
     * Dispatches an AccessibilityEvent to the View first
     * and then to its children for adding their text content to the event
     * */
    @Override
    // dispatchPopulateAccessibilityEvent
    public boolean dispatchPopulateAccessibilityEvent( AccessibilityEvent event ) {
        event.getText().add( getWindDirectionAndSpeedText() ); return true;
    }

    /* Other Methods */

    /**
     * Converts the passed in dp value to a pixel float value.
     *
     * @param dp    The integer dp value.
     *
     * @return      A float representing the pixel value of the passed in dp.
     * */
    // method dpToPx
    private float dpToPx( int dp ) {
        return ( dp * ( getResources().getDisplayMetrics().densityDpi / 160f ) );
    }

    /**
     * Gets the smaller value of the width and the height of this view.
     * */
    // begin method getSmallerLength
    private int getSmallerLength() {

        // 0. get total width
        // 1. get total height
        // 2. return the smaller

        // 0. get total width

        int totalWidth = this.getMeasuredWidth() + getPaddingLeft() + getPaddingRight();

        // 1. get total height

        int totalHeight = this.getMeasuredHeight() + getPaddingTop() + getPaddingBottom();

        // 2. return the smaller

        return ( totalWidth < totalHeight ) ? totalWidth : totalHeight;

    } // end method getSmallerLength

    /**
     * Gets the point we will use as the horizontal origin.
     *
     * @param outerCircleRadius The radius of the outer circle,
     *                          needed to find the origin when the width of the screen
     *                          is larger than screen height.
     *
     * @return The horizontal, or X, origin.
     * */
    // begin method getOriginX
    private float getOriginX( float outerCircleRadius ) {

        // 0. if the width is larger
        // 0a. origin is left padding + (total width)/4 - outer radius
        // 1. otherwise
        // 1a. origin is left padding

        // 0. if the width is larger
        // 0a. origin is left padding + (total width)/4 - outer radius

        if ( isWidthLarger() == true ) {
            return getPaddingLeft() + getTotalWidth() / 4 - outerCircleRadius;
        }

        // 1. otherwise
        // 1a. origin is left padding

        else { return getPaddingLeft(); }

    } // end method getOriginX

    /**
     * Gets the point we will use as the vertical origin.
     *
     * @param outerCircleRadius The radius of the outer circle,
     *                          needed to find the origin when the height of the screen
     *                          is larger than screen width.
     *
     * @return The vertical, or Y, origin - mostly the top padding.
     * */
    // method getOriginY
    private float getOriginY( float outerCircleRadius ) {

        // 0. if the height is larger
        // 0a. origin is top padding + (total height)/4 - outer radius
        // 1. otherwise
        // 1a. origin is top padding

        // 0. if the height is larger
        // 0a. origin is top padding + (total height)/4 - outer radius

        if ( isWidthLarger() == false /* so the height is the larger one */ ) {
            return getPaddingTop() + getTotalHeight() / 4 - outerCircleRadius;
        }

        // 1. otherwise
        // 1a. origin is top padding

        return getPaddingTop();
    }

    /**
     * Helper method to check which is larger, width or height
     * */
    // begin method isWidthLarger
    private boolean isWidthLarger() {

        // 0. get total width
        // 1. get total height
        // 2. return whether the width is larger

        // 0. get total width
        // 1. get total height
        // 2. return whether the width is larger

        return getTotalWidth() > getTotalHeight();

    } // end method isWidthLarger

    /** Helper method to get total width. */
    // method getTotalWidth
    private int getTotalWidth() {
        return this.getMeasuredWidth() + getPaddingLeft() + getPaddingRight();
    }

    /** Helper method to get total height. */
    // method getTotalHeight
    private int getTotalHeight() {
        return this.getMeasuredHeight() + getPaddingTop() + getPaddingBottom();
    }

    /** 
     * Helper method to animate the rotation of the arrow. 
     * 
     * @param previousAngle The angle to rotate from, in degrees.
     * @param targetAngle The angle to rotate to, in degrees.
     * */
    // begin method animateArrowRotation
    private void animateArrowRotation( final float previousAngle, float targetAngle ) {

        // 0. if there is an animation happening, stop it
        // 1. we are about to animate
        // 1a. animate from the previous angle to the target angle
        // 1b. for a time dependent on the start and end angles
        // 1c. and on every animation update
        // 1c1. store the current value of the animation as the arrow angle we should draw
        // 1c2. invalidate, thus redraw
        // 2. animate!

        // we'll animate only in compatible versions

        // begin if we're on at least Honeycomb
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {

            // 0. if there is an animation happening, stop it

            if ( mArrowValueAnimator != null ) { mArrowValueAnimator.cancel(); }

            // 1. we are about to animate

            // 1a. animate from the previous angle to the target angle

            mArrowValueAnimator = ValueAnimator.ofFloat( previousAngle, targetAngle );

            // 1b. for a time dependent on the start and end angles

            setAnimationDurationToUse( previousAngle, targetAngle, 360f );

            mArrowValueAnimator.setDuration( getAnimationDurationToUse() );

            // 1c. and on every animation update

            // begin addUpdateListener
            mArrowValueAnimator.addUpdateListener(

                    // begin new ValueAnimator.AnimatorUpdateListener
                    new ValueAnimator.AnimatorUpdateListener() {

                        @Override
                        // begin onAnimationUpdate
                        public void onAnimationUpdate( ValueAnimator valueAnimator ) {

                            // begin if we're on at least Honeycomb
                            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {

                                // 1c1. store the current value of the animation
                                //      as the arrow angle we should draw

                                mArrowAngleToDraw = ( float ) valueAnimator.getAnimatedValue();

                                // 1c2. invalidate, thus redraw

                                WindDirectionAndSpeedView.this.invalidate();

                            } // end if we're on at least Honeycomb

                        } // end onAnimationUpdate

                    } // end new ValueAnimator.AnimatorUpdateListener

            ); // end addUpdateListener

            // 2. animate!

            mArrowValueAnimator.start();

        } // end if we're on at least Honeycomb

        // otherwise just draw the final value of the arrow angle
        else { mArrowAngleToDraw = mArrowAngle; invalidate(); }

    } // end method animateArrowRotation

    /**
     * Sets the animation duration to use to be dependent on
     * the amount of change between the animation start and end values.
     *
     * Courtesy of https://www.intertech.com/Blog/android-custom-view-tutorial-part-4-animation/.
     *
     * @param animationStartValue   The value that the animation starts from
     * @param animationEndValue     The value that the animation ends at
     * @param maxValue              The maximum possible value that these two values can be
     *
     * */
    // begin method setAnimationDurationToUse
    private void setAnimationDurationToUse( float animationStartValue, float animationEndValue,
                                            float maxValue ) {

        // 0. get the difference between the two values
        // 1. the time used should be proportional to
        // how much difference there is between the values

        // 0. get the difference between the two values

        float valueDifference = Math.abs( animationEndValue - animationStartValue );

        // 1. the time used should be proportional to
        // how much difference there is between the values

        mAnimationDurationToUse = ( long ) ( mAnimationDuration * ( valueDifference / maxValue ) );

    } // end method setAnimationDurationToUse

    // begin method getAnimationDurationToUse
    private long getAnimationDurationToUse() {
        return mAnimationDurationToUse;
    }

    /**
     * Animates the speed text.
     *
     * More specifically, animates the speed text's alpha and its translationX.
     * */
    // begin method animateSpeedText
    private void animateSpeedText() {

        // 0. animate the alpha
        // 1. animate the translationX

        // 0. animate the alpha

        animateSpeedTextAlpha( 0, 255 ); // alpha ranges from 0 to 255, both inclusive

        // 1. animate the translationX

        Rect speedTextBoundsRect = new Rect();
        mTextPaint.getTextBounds( mSpeedText, 0, mSpeedText.length(), speedTextBoundsRect );

        mSpeedTextLeftXToDraw = mSpeedTextLeftX - speedTextBoundsRect.width();

        animateSpeedTextTranslationX(
                mSpeedTextLeftX - speedTextBoundsRect.width(), mSpeedTextLeftX
        );

    } // end method animateSpeedText

    /**
     * Helper method to animate the alpha of the speed text.
     *
     * @param startAlpha    The alpha value to start animating from
     * @param endAlpha      The alpha value to animate to
     * */
    // begin method animateSpeedTextAlpha
    private void animateSpeedTextAlpha( int startAlpha, int endAlpha ) {

        // 0. if there is an animation happening, stop it
        // 1. we are about to animate
        // 1a. animate from the start alpha to the end alpha
        // 1b. for a time dependent on the animation duration we should use
        // 1c. and on every animation update
        // 1c1. store the current value of the animation as the alpha value we should draw
        // 1c2. invalidate, thus redraw
        // 2. animate!

        // we'll animate only on compatible versions

        // begin if we're on at least Honeycomb
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {

            // 0. if there is an animation happening, stop it

            if ( mSpeedAlphaValueAnimator != null ) { mSpeedAlphaValueAnimator.cancel(); }

            // 1. we are about to animate
            // 1a. animate from the start alpha to the end alpha
            // 1b. for a time dependent on the animation duration we should use

            mSpeedAlphaValueAnimator = ValueAnimator.ofInt( startAlpha, endAlpha )
                    .setDuration( getAnimationDurationToUse() );

            // 1c. and on every animation update

            // begin mSpeedAlphaValueAnimator.addUpdateListener
            mSpeedAlphaValueAnimator.addUpdateListener(

                    // begin new ValueAnimator.AnimatorUpdateListener
                    new ValueAnimator.AnimatorUpdateListener() {

                        @Override
                        // begin onAnimationUpdate
                        public void onAnimationUpdate( ValueAnimator valueAnimator ) {

                            // begin if we're on at least Honeycomb
                            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {

                                // 1c1. store the current value of the animation
                                // as the alpha value we should draw

                                mSpeedTextAlphaToDraw = ( int ) valueAnimator.getAnimatedValue();

                                // 1c2. invalidate, thus redraw

                                WindDirectionAndSpeedView.this.invalidate();

                            } // end if we're on at least Honeycomb

                        } // end onAnimationUpdate

                    } // end new ValueAnimator.AnimatorUpdateListener

            ); // end mSpeedAlphaValueAnimator.addUpdateListener

            // 2. animate!

            mSpeedAlphaValueAnimator.start();

        } // end if we're on at least Honeycomb

        // otherwise just use the final value of the alpha
        else { mSpeedTextAlphaToDraw = 255; invalidate(); }

    } // end method animateSpeedTextAlpha

    /**
     * Helper method to animate the translationX of the speed text.
     *
     * @param startTranslationX The translationX to start animating from
     * @param endTranslationX   The translationX to animate to
     * */
    // begin method animateSpeedTextTranslationX
    private void animateSpeedTextTranslationX( float startTranslationX, float endTranslationX ) {

        // 0. if there is an animation happening, stop it
        // 1. we are about to animate
        // 1a. animate from the start translationX to the end translationX
        // 1b. for a time dependent on the animation duration we should use
        // 1c. and on every animation update
        // 1c1. store the current value of the animation as the translationX value we should draw
        // 1c2. invalidate, thus redraw
        // 2. animate!

        // begin if we're on at least Honeycomb
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {

            // 0. if there is an animation happening, stop it

            if ( mSpeedTranslationXValueAnimator != null ) { mSpeedTranslationXValueAnimator.cancel(); }

            // 1. we are about to animate
            // 1a. animate from the start translationX to the end translationX
            // 1b. for a time dependent on the animation duration we should use

            mSpeedTranslationXValueAnimator =
                    ValueAnimator.ofFloat( startTranslationX, endTranslationX )
                    .setDuration( getAnimationDurationToUse() );

            // 1c. and on every animation update

            // begin mSpeedTranslationXValueAnimator.addUpdateListener
            mSpeedTranslationXValueAnimator.addUpdateListener(

                    // begin new ValueAnimator.AnimatorUpdateListener
                    new ValueAnimator.AnimatorUpdateListener() {

                        @Override
                        // begin onAnimationUpdate
                        public void onAnimationUpdate( ValueAnimator valueAnimator ) {

                            // begin if we're on at least Honeycomb
                            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {

                                // 1c1. store the current value of the animation
                                // as the translationX value we should draw

                                mSpeedTextLeftXToDraw = ( float ) valueAnimator.getAnimatedValue();

                                // 1c2. invalidate, thus redraw

                                WindDirectionAndSpeedView.this.invalidate();

                            } // end if we're on at least Honeycomb

                        } // end onAnimationUpdate

                    } // end new ValueAnimator.AnimatorUpdateListener

            ); // end mSpeedTranslationXValueAnimator.addUpdateListener

            // 2. animate!

            mSpeedTranslationXValueAnimator.start();

        } // end if we're on at least Honeycomb

        // otherwise just draw the final translationX
        else { mSpeedTextLeftXToDraw = mSpeedTextLeftX; invalidate(); }

    } // end method animateSpeedTextTranslationX

    /**
     * Gets the length of a given piece of text
     *
     * @param text      The text to get the length of
     * @param textPaint The {@link TextPaint} we will use to determine text length
     *
     * @return The length of the given text
     * */
    private float getTextLength( String text, TextPaint textPaint ) {
        return textPaint.measureText( text );
    }

    /** Initializes this view. */
    // begin method initView
    private void initView( Context context, AttributeSet attrs ) {

        // 0. initialize
        // 0a. paints
        // 0b. paths
        // 0c. rectangles
        // 0d. matrices
        // 0e. animators
        // 1. initialize member variables from the XML
        // 2. initialize the animation duration we will use to be the smallest possible
        // since we are just starting
        // 3. make the view focusable for accessibility by making it
        // 3a. focusable
        // 3b. focusable in touch mode
        // 3c. clickable

        // 0. initialize things

        // 0a. paints

        mCirclePaint = new Paint();

        mTextPaint = new TextPaint( mCirclePaint );

        mNorthIndicatorPaint = new Paint();

        mArrowPaint = new Paint();

        mSpeedPaint = new Paint();

        // 0b. paths

        mArrowPath = new Path();

        // 0c. rectangles

        mArrowBottomArcRectF = new RectF();

        // 0d. matrices

        mArrowPathRotationMatrix = new Matrix();

        // 0e. animators

        mArrowValueAnimator = null;

        mSpeedAlphaValueAnimator = null;

        mSpeedTranslationXValueAnimator = null;

        // 1. initialize member variables from the XML

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.WindDirectionAndSpeedView, 0, 0
        );

        // begin trying to get things from XML
        try {

            mOuterCircleColor = a.getColor( R.styleable.WindDirectionAndSpeedView_outerCircleColor,
                    getResources().getColor( R.color.sunshine_blue )
            );

            mInnerCircleColor = a.getColor( R.styleable.WindDirectionAndSpeedView_innerCircleColor,
                    getResources().getColor( android.R.color.white )
            );

            mRadiusDifference = a.getDimension( R.styleable.WindDirectionAndSpeedView_radiusDifference,
                     DEFAULT_RADIUS_DIFFERENCE_DP
            );

            mNorthIndicatorText = a.getString( R.styleable.WindDirectionAndSpeedView_northIndicatorText );

            mNorthIndicatorColor = a.getColor( R.styleable.WindDirectionAndSpeedView_northIndicatorColor,
                    getResources().getColor( android.R.color.black )
            );

            mNorthIndicatorStrokeWidth = a.getFloat(
                    R.styleable.WindDirectionAndSpeedView_northIndicatorStrokeWidth, 2.0f
            );

            mArrowColor = a.getColor( R.styleable.WindDirectionAndSpeedView_arrowColor,
                    getResources().getColor( R.color.sunshine_dark_blue )
            );

            mArrowAngle = a.getFloat( R.styleable.WindDirectionAndSpeedView_arrowAngle, 0f );

            mAnimationDuration = a.getInt(
                    R.styleable.WindDirectionAndSpeedView_animationDuration,
                    getResources().getInteger( android.R.integer.config_shortAnimTime ) * 5
                    // TODO: 9/18/16 I hope this 1500 ms value isn't gratuitous
            );

            mSpeedColor = a.getColor( R.styleable.WindDirectionAndSpeedView_speedColor,
                    getResources().getColor( android.R.color.black )
            );

            mSpeedText = a.getString( R.styleable.WindDirectionAndSpeedView_speedText );

            if ( mSpeedText == null ) { mSpeedText = ""; }

            mSpeedStrokeWidth = a.getFloat(
                    R.styleable.WindDirectionAndSpeedView_speedStrokeWidth, 2.0f
            );

        } // end trying to get things from XML

        finally { a.recycle(); }

        // 2. initialize the animation duration we will use to be the smallest possible
        // since we are just starting

        setAnimationDurationToUse( -1, -1, -1 );

        // 3. make the view focusable for accessibility by making it

        // 3a. focusable

        // Set whether this view can receive the focus.
        setFocusable( true );

        // 3b. focusable in touch mode

        // Set whether this view can receive focus while in touch mode.
        setFocusableInTouchMode( true );

        // 3c. clickable

        // Enables or disables click events for this view.
        // Visually reacts to user's clicks.
        setClickable( true );

    } // end method initView

    /** Installs the accessibility delegate into this view. */
    // begin method installAccessibilityDelegate
    private void installAccessibilityDelegate() {

        // begin ViewCompat.setAccessibilityDelegate
        ViewCompat.setAccessibilityDelegate(

                this,

                // begin new AccessibilityDelegateCompat
                new AccessibilityDelegateCompat() {

                    @Override
                    /**
                     * Gives a chance to the host View to
                     * populate the accessibility event with its text content
                     * */
                    // begin onPopulateAccessibilityEvent
                    public void onPopulateAccessibilityEvent( View host, AccessibilityEvent event ) {

                        // 0. super stuff
                        // 1. add the direction and speed text

                        // 0. super stuff

                        super.onPopulateAccessibilityEvent( host, event );

                        // 1. add the direction and speed text

                        event.getText().add( getWindDirectionAndSpeedText() );

                    } // end onPopulateAccessibilityEvent

                    @Override
                    /**
                     * Initializes an AccessibilityNodeInfoCompat( representing node of the window
                     * content as well as actions that can be requested from its source) with
                     * information about the host view.
                     * */
                    // begin onInitializeAccessibilityNodeInfo
                    public void onInitializeAccessibilityNodeInfo( View host,
                                                                   AccessibilityNodeInfoCompat info ) {

                        // 0. super stuff
                        // 1. put the direction and speed text in the node info

                        // 0. super stuff

                        super.onInitializeAccessibilityNodeInfo( host, info );

                        // 1. put the direction and speed text in the node info

                        info.setText( getWindDirectionAndSpeedText() );

                    } // end onInitializeAccessibilityNodeInfo

                } // end new AccessibilityDelegateCompat

        ); // end ViewCompat.setAccessibilityDelegate

    } // begin method installAccessibilityDelegate

    // getter for the wind direction and speed text for accessibility
    public String getWindDirectionAndSpeedText() {
        return mWindDirectionAndSpeedTextForAccessibility;
    }

    // setter for the wind direction and speed text for accessibility
    public void setWindDirectionAndSpeedTextForAccessibility( String windDirectionAndSpeedTextForAccessibility ) {
        this.mWindDirectionAndSpeedTextForAccessibility = windDirectionAndSpeedTextForAccessibility;
    }

    /* INNER CLASSES */

} // end class WindDirectionSpeedView