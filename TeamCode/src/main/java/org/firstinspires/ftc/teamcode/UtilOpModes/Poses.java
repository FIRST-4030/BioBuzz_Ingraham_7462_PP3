package org.firstinspires.ftc.teamcode.UtilOpModes;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;

public class Poses {
    //constructor and other important stuff
    private final PoseFactory poseFactory = PoseFactory.degrees();
    public Poses(){}

    //test auto poses
    public final Pose testStart = poseFactory.of(24, 24, 0);
    public final Pose corner1 = poseFactory.of(48, 24, 0);
    public final Pose corner2 = poseFactory.of(48, 48, 0);
    public final Pose corner3 = poseFactory.of(24, 48, 0);



    //other test poses
    public final Pose testStart2 = poseFactory.of(56, 8, 0);
    public final Pose testMid = poseFactory.of(18, 63, 0);
    public final Pose testControlPoint = poseFactory.of(14,26, 0);



    //Poses for PP3 auto
    public final Pose autoStartPos = poseFactory.of(63, 5, 90);
    public final Pose shootPos = poseFactory.of(56, 20, 90);

    public final Pose collectConPose = poseFactory.of(35, 10 , 90);
    public final Pose collectPos = poseFactory.of(13, 10, 90);

    public final Pose parkContPose = poseFactory.of(20, 49 , 90);
    public final Pose parkPos = poseFactory.of(9, 94, 90);
}
