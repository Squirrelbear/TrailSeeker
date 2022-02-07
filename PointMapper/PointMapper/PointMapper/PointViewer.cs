using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework;

namespace PointMapper
{
    public class PointViewer
    {
        List<Vector2> points;
        List<Vector2> unscalePoints;
        Rectangle bounds;
        Rectangle realBounds;
        Vector2 lastPoint;
        float scaleX, scaleY;

        // extra test variables
        SpriteFont font;
        List<Vector2> unaddedPoints;
        Texture2D blank;
        int curPoint = 0;

        public PointViewer(Game1 game, int width, int height)
        {
            realBounds = new Rectangle(0, 0, width, height);
            bounds = new Rectangle(-10, -10, 20, 20);
            scaleX = (bounds.Right - bounds.Left) * 1.0f / (realBounds.Right - realBounds.Left);
            scaleY = (bounds.Bottom - bounds.Top) * 1.0f / (realBounds.Bottom - realBounds.Top);

            points = new List<Vector2>();
            points.Add(rescalePoint(new Vector2(0, 0)));
            unscalePoints = new List<Vector2>();
            unscalePoints.Add(new Vector2(0, 0));

            lastPoint = new Vector2(0, 0);

            // contains x = distance and y = rotation
            unaddedPoints = new List<Vector2>();
            unaddedPoints.Add(new Vector2(5, 0));
            unaddedPoints.Add(new Vector2(10, 50));
            unaddedPoints.Add(new Vector2(50, 180));

            font = game.Content.Load<SpriteFont>("hugeFont");
            blank = new Texture2D(game.GraphicsDevice, 1, 1, false, SurfaceFormat.Color);
            blank.SetData(new[]{Color.White});
        }

        public void addPoint()
        {
            if (curPoint >= unaddedPoints.Count)
                return;

            float d = unaddedPoints[curPoint].X;
            float r = unaddedPoints[curPoint].Y;
            addPoint(d, r);
            curPoint++;
        }

        public void addPoint(float distance, float rotation)
        {
            Vector2 newPoint = determinePoint(lastPoint, distance, rotation);

            Rectangle testRect = new Rectangle((int)(bounds.X * 1.1f), (int)(bounds.Y * 1.1f), 
                                (int)(bounds.Width * 0.8f), (int)(bounds.Height * 0.8f));


            unscalePoints.Add(newPoint);
            if (testRect.Contains((int)newPoint.X, (int)newPoint.Y))
            {
                // new point is in the existing area just add the point to the list
                points.Add(rescalePoint(newPoint));
            }
            else
            {
                // new point is outside the safe bounds of the circle. 
                // need to perform a scale update to all points with new bounds
                if (newPoint.X < testRect.X)
                {
                    // absolute in case the point is actually within the safety barrier
                    float distChange = Math.Abs(bounds.X - newPoint.X) + 10;
                    bounds.X -= (int)distChange;
                    bounds.Width += (int)distChange;
                }
                else if (newPoint.X > testRect.Right)
                {
                    float distChange = (Math.Abs(newPoint.X - bounds.X) + bounds.Width) * 1.1f;
                    bounds.Width += (int)distChange;
                }

                if (newPoint.Y < testRect.Y)
                {
                    // absolute in case the point is actually within the safety barrier
                    float distChange = (Math.Abs(bounds.Y - newPoint.Y) + bounds.Height) * 1.1f;
                    bounds.Y -= (int)distChange;
                    bounds.Height += (int)distChange;
                }
                else if (newPoint.Y > testRect.Bottom)
                {
                    float distChange = Math.Abs(newPoint.Y - bounds.Y) + 10;
                    bounds.Height += (int)distChange;
                }

                scaleX = (bounds.Right - bounds.Left) * 1.0f / (realBounds.Right - realBounds.Left);
                scaleY = (bounds.Bottom - bounds.Top) * 1.0f / (realBounds.Bottom - realBounds.Top);
                
                points = new List<Vector2>();
                foreach (Vector2 uP in unscalePoints)
                {
                    points.Add(rescalePoint(uP));
                }
            }
        }

        private Vector2 rescalePoint(Vector2 point)
        {
            Vector2 newPoint = new Vector2(point.X, point.Y);
            newPoint.X = realBounds.Left + (point.X - bounds.Left) / scaleX;
            newPoint.Y = realBounds.Top + (point.Y - bounds.Top) / scaleY;
            return newPoint;
        }

        // returns new point location relative to old location
        public Vector2 determinePoint(Vector2 lastPoint, float distance, float rotation)
        {
            // TODO: Note that this will need changing if radians is the value
            Vector2 result = new Vector2(lastPoint.X, lastPoint.Y);
            result.X += distance * (float)Math.Cos(MathHelper.ToRadians(rotation - 90));
            result.Y += distance * (float)Math.Sin(MathHelper.ToRadians(rotation - 90));

            return result;
        }

        public void drawPointList(SpriteBatch spriteBatch)
        {
            DrawRect(spriteBatch, 1, realBounds);

            for (int i = 0; i < points.Count; i++)
            {
                spriteBatch.DrawString(font, (i + 1) + "", points[i], Color.Black);
            }
        }


        private void DrawRect(SpriteBatch batch, float width, Rectangle rect)
        {
            DrawLine(batch, width, Color.Black, new Vector2(rect.Left, rect.Top), new Vector2(rect.Right, rect.Top));
            DrawLine(batch, width, Color.Black, new Vector2(rect.Right, rect.Top), new Vector2(rect.Right, rect.Bottom));
            DrawLine(batch, width, Color.Black, new Vector2(rect.Right, rect.Bottom), new Vector2(rect.Left, rect.Bottom));
            DrawLine(batch, width, Color.Black, new Vector2(rect.Left, rect.Bottom), new Vector2(rect.Left, rect.Top));
        }

        private void DrawLine(SpriteBatch batch, float width, Color color, Vector2 point1, Vector2 point2)
        {
            float angle = (float)Math.Atan2(point2.Y - point1.Y, point2.X - point1.X);
            float length = Vector2.Distance(point1, point2);

            batch.Draw(blank, point1, null, color,
                       angle, Vector2.Zero, new Vector2(length, width),
                       SpriteEffects.None, 0);
        }
    }
}
