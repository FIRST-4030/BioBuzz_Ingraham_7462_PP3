package org.firstinspires.ftc.teamcode;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;

import org.firstinspires.ftc.teamcode.pedro.procedures.ForesightTuner;

public class Poses {
    private final double FORWARD = 90;
    private final double ROBOT_SIZE = 17;
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



    //left side park
    public final Pose audienceStartPos = poseFactory.of(63, 5, FORWARD);
    public final Pose audienceShootPos = poseFactory.of(56, 20, FORWARD);

    public final Pose audienceCollectContPose = poseFactory.of(35, 10 , FORWARD);
    public final Pose audienceCollectPos = poseFactory.of(13, 10, FORWARD + 90);

    public final Pose audienceParkContPose = poseFactory.of(20, 49 , FORWARD);
    public final Pose audienceParkPos = poseFactory.of(9, 94, FORWARD - 90);

    //worst case
    public final Pose parkNoHeading = poseFactory.of(9, 94, FORWARD);
    public final Pose zero = poseFactory.of(0,0, FORWARD);

    //right side park
    public final Pose rightStartPos = poseFactory.of(78, ROBOT_SIZE, FORWARD);
    public final Pose shootIG = poseFactory.of(82, 20, FORWARD);
    public final Pose preParkPos = poseFactory.of(144 - ROBOT_SIZE, ROBOT_SIZE, FORWARD);
    public final Pose RSParkPos = poseFactory.of(144 - ROBOT_SIZE, 30, FORWARD);




    //debug poses
    public final Pose testLineStart = poseFactory.of(0,0, FORWARD);
    public final Pose testLineEnd = poseFactory.of(0, 20, FORWARD);

}
