package org.firstinspires.ftc.teamcode.OpModes;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.instant;
import static com.pedropathing.ivy.commands.Commands.lazy;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.commands.Commands.waitUntil;
import static com.pedropathing.ivy.groups.Groups.deadline;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.generalUtilities.Blackboard;
import org.firstinspires.ftc.teamcode.BiobuzzShooter;
import org.firstinspires.ftc.teamcode.Collector;
import org.firstinspires.ftc.teamcode.ControlHub;
import org.firstinspires.ftc.teamcode.Limelight;
import org.firstinspires.ftc.teamcode.Poses;

import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.stream.IntStream;

@Disabled
@Autonomous(name = "two tip auto")
public class hopefullyTwoTipAuto extends OpMode {

    //April Tags corresponding numbers
    private final int[] BLUE_SCORING = {38, 39, 40, 41};
    private final int[] BLUE_AUDIENCE = {42, 43, 44, 45};
    private final int[] RED_SCORING = {34, 35, 36, 37};
    private final int[] RED_AUDIENCE = {31, 32, 33, 34};

    //What auto should do
    private final boolean shouldCollect = false;

    //Components and stuff
    ControlHub controlHub;
    private Follower follower;
    private Collector collector;
    private Limelight ll;
    private BiobuzzShooter shooter;
    private Limelight3A limelight;
    private LLResult llResult;

    //bank of poses
    Poses poses = new Poses();

    //Path methods
    private Path startToShoot() {
        return line(poses.startPosLeft, poses.shootPosLeft).linear(poses.startPosLeft, poses.shootPosLeft);
    }

    private Path collectToShoot() {
        return line(poses.collectPosLeft, poses.shootPosLeft).linear(poses.collectPosLeft, poses.shootPosLeft);
    }

    private Path shootToPreCollect() {
        return line(poses.shootPosLeft, poses.preCollectPoseLeft).facingPoint(poses.collectPosLeft);
    }

    private Path preCollectToCollect() {
        return line(poses.preCollectPoseLeft, poses.collectPosLeft).facingPoint(poses.collectPosLeft);
    }

    private Path shootToOppositeSideShoot(){
        return curve(poses.shootPosLeft, poses.middle, poses.oppositeSideShootPosLeft).linear(poses.shootPosLeft, poses.oppositeSideShootPosLeft);
    }

    private Path robotToPark() {
        Pose currentPose = follower.pose();
        return curve(currentPose, poses.of(24, 24), poses.parkPosLeft).linear(currentPose, poses.parkPosLeft);
    }

    /**
     * Set stuff up
     **/
    @Override
    public void init() {
        //initialize components
        controlHub = new ControlHub(hardwareMap);
        collector = new Collector(hardwareMap);
        ll = new Limelight(hardwareMap);
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        shooter = new BiobuzzShooter(hardwareMap, ll);

        //april tag pipeline
        limelight.pipelineSwitch(2);

        //Reset movements
        Scheduler.reset();
        follower = controlHub.getRobotSpecificPedroConstants();

        //sets pose to "start"
        follower.setPose(poses.startPosRight);
        follower.update();
    }

    /**
     * set team
     */
    @Override
    public void init_loop() {
        if (gamepad1.right_bumper) {
            if (gamepad1.bWasPressed()) {
                Blackboard.setAlliance(Blackboard.Alliance.RED);
            } else if (gamepad1.xWasPressed()) {
                Blackboard.setAlliance(Blackboard.Alliance.BLUE);
            }
        }

        telemetry.addData("[B: If should collect]", shouldCollect);
        telemetry.addLine("[RB + B: RED ALLIANCE]");
        telemetry.addData("Alliance", Blackboard.getAlliance());
    }

    /**
     * Runs the routine
     **/
    @Override
    public void start() {
        schedule(autoRoutine());
    }

    /**
     * Runs every something frame?
     **/
    @Override
    public void loop() {
        shooter.overridePower();
        collector.setCollector();
        llResult = limelight.getLatestResult();
        Scheduler.execute();
        follower.update();
    }

    public BooleanSupplier isAtVelocity = () -> shooter.atSpeed();

    /**
     * logic for shooting
     *
     * @return a sequential well well well
     */
    private Command shoot(int[] tagsToLookFor){
        return sequential(
            instant(()->{
                shooter.setShooterServos(false);
                shooter.resetTargetVelocity();
                shooter.setBackSpinPower(0.8, 0.8);
                shooter.setBackSpinTargetVelocity(5000);
                shooter.setTargetVelocity(-1460);
            }),
            waitUntil(() -> canSeeID(BLUE_SCORING) || canSeeID(RED_AUDIENCE)),
            lazy(() -> waitUntil(isAtVelocity)),
            waitMs(2000),
            instant(() -> shooter.setShooterServos(true))
        );
    }

    /**
     * The sequential which contains wanted movements
     *
     * @return sequential with wanted movements
     */
    private Command autoLogic() {
        return sequential(
                instant(() -> collector.setCollectorSpeed(Collector.CollectorMode.COLLECT)),
                follow(follower, startToShoot()),
                instant(()-> schedule(shoot(IntStream.concat(Arrays.stream(BLUE_SCORING), Arrays.stream(RED_AUDIENCE)).toArray()))),
                follow(follower, shootToPreCollect()),
                follow(follower, preCollectToCollect()),
                follow(follower , collectToShoot()),
                instant(() -> {
                    if (canSeeID(BLUE_SCORING) || canSeeID(RED_AUDIENCE)){
                        schedule(shoot(IntStream.concat(Arrays.stream(BLUE_SCORING), Arrays.stream(RED_AUDIENCE)).toArray()));
                    } else {
                        follow(follower, shootToOppositeSideShoot());
                        schedule(shoot(IntStream.concat(Arrays.stream(BLUE_AUDIENCE), Arrays.stream(RED_SCORING)).toArray()));
                    }
                })
                //TODO color blob detection for collecting more or flower collection for an extra tip
        );
    }


    /**
     * Contains the logic for parking automatically
     * Important Lambdas:   deadline runs two or more processes in parallel and stops all processes once one finishes
     * lazy doesn't finalize when the code runs instead it finalizes it values when it's ran
     *
     * @return returns a sequential containing logic for the routine
     **/
    private Command autoRoutine() {
        return sequential(
                deadline(
                        waitMs(20000),
                        autoLogic()
                ),
                lazy(() -> follow(follower, robotToPark()))
        );
    }

    /**
     * can limelight see IDs
     *
     * @param IDs an array of the wanted ids corresponding numbers
     *
     * @return returns true if can see wanted ID(s)
     */
    public boolean canSeeID(int[] IDs) {
        List<LLResultTypes.FiducialResult> tags = llResult.getFiducialResults();
        if (tags == null) return false;
        for (LLResultTypes.FiducialResult tag : tags) {
            for (int ID: IDs) {
                if(ID == tag.getFiducialId()) return true;
            }
        }
        return false;
    }
}